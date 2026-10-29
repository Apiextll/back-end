// Lógica da partida: polling do /status e renderização de cada estado.
const fmt = (s) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;

const Jogo = {
    codigo: null, jogadorId: null, local: false,
    poller: null, ticker: null, busy: false,
    ultimoStatus: null, contagemInicio: 0,
    qId: null, qInicio: 0, tempoQ: 15, travado: false, finalizado: false,

    iniciar(codigo, jogadorId, local) {
        this.parar();
        Object.assign(this, {
            codigo, jogadorId, local, ultimoStatus: null, qId: null, travado: false, finalizado: false,
        });
        this.poller = setInterval(() => this.poll(), 1000);
        this.ticker = setInterval(() => this.tick(), 250);
        this.poll();
    },

    parar() {
        clearInterval(this.poller);
        clearInterval(this.ticker);
        this.poller = this.ticker = null;
    },

    async poll() {
        if (this.busy || this.finalizado) return;
        this.busy = true;
        try {
            this.render(await Api.status(this.codigo, this.jogadorId));
        } catch (e) {
            aviso(e.message);
        } finally {
            this.busy = false;
        }
    },

    render(s) {
        if (s.status !== this.ultimoStatus) {
            if (s.status === 'CONTAGEM_REGRESSIVA') this.contagemInicio = Date.now();
            this.ultimoStatus = s.status;
        }
        if (this.local && s.turnoAtualJogadorId) this.jogadorId = s.turnoAtualJogadorId;

        switch (s.status) {
            case 'AGUARDANDO_JOGADORES':
            case 'CONTAGEM_REGRESSIVA':
                ir('espera');
                this.renderEspera(s);
                break;
            case 'EM_ANDAMENTO':
                ir('jogo');
                this.renderJogo(s);
                break;
            case 'INTERVALO_TRANSICAO':
                ir('transicao');
                $('trans-titulo').textContent = `Turno de ${s.nomeJogadorDaVez || 'jogador'} encerrado`;
                this.placar($('placar-trans'), s);
                break;
            case 'FINALIZADA':
                this.finalizar(s);
                break;
        }
    },

    placar(box, s) {
        box.innerHTML = '';
        [s.jogador1, s.jogador2].forEach((j) => {
            if (!j) return;
            const vez = j.id === s.turnoAtualJogadorId || (!this.local && j.id === this.jogadorId);
            const d = mk('div', 'jogador' + (vez ? ' da-vez' : ''));
            d.append(mk('span', '', j.nome), mk('b', '', `${j.pontuacaoTotal} pts`));
            box.append(d);
        });
    },

    renderEspera(s) {
        const contando = s.status === 'CONTAGEM_REGRESSIVA';
        $('espera-titulo').textContent = `Sala ${s.codigo}`;
        const box = $('jogadores');
        box.innerHTML = '';
        [s.jogador1, s.jogador2].forEach((j) => {
            const d = mk('div', 'jogador' + (j && j.pronto ? ' pronto' : ''));
            d.append(mk('span', '', j ? j.nome : 'Aguardando jogador...'));
            if (j) d.append(mk('b', '', j.pronto ? 'Pronto' : 'Não pronto'));
            box.append(d);
        });
        const eu = [s.jogador1, s.jogador2].find((j) => j && j.id === this.jogadorId);
        const btn = $('btn-pronto');
        btn.hidden = contando || this.local || !eu;
        btn.firstElementChild.textContent = eu && eu.pronto ? 'CANCELAR PRONTO' : 'ESTOU PRONTO';
        $('btn-sair').hidden = contando;
        const c = $('contagem');
        c.hidden = !contando;
        if (contando) {
            const n = Math.max(1, 3 - Math.floor((Date.now() - this.contagemInicio) / 1000));
            c.textContent = this.local && s.nomeJogadorDaVez ? `${s.nomeJogadorDaVez}: ${n}` : `Prepare-se: ${n}`;
        }
    },

    renderJogo(s) {
        this.tempoQ = s.tempoPorQuestaoSegundos || 15;
        $('t-total').textContent = fmt(s.tempoRestanteTurnoSegundos);
        $('vez').textContent = this.local ? `Vez de ${s.nomeJogadorDaVez}` : '';
        this.placar($('placar'), s);
        if (!this.travado && s.questaoAtual && s.questaoAtual.id !== this.qId) this.mostrarQuestao(s.questaoAtual);
    },

    mostrarQuestao(q) {
        this.qId = q.id;
        this.qInicio = Date.now();
        $('feedback').textContent = '';
        $('q-meta').textContent = `${q.materia} - ${q.dificuldade} - ${q.pontosBase} pts`;
        $('enunciado').textContent = q.enunciado;
        const box = $('alternativas');
        box.innerHTML = '';
        q.alternativas.forEach((texto, i) => {
            const b = mk('button', 'alt', `${'ABCD'[i]}) ${texto}`);
            b.type = 'button';
            b.onclick = () => this.responder(i);
            box.append(b);
        });
    },

    // Contagem regressiva da questão (15s). Ao zerar, envia resposta em branco (-1).
    tick() {
        if (!$('tela-jogo').classList.contains('ativa') || this.qId === null || this.travado) return;
        const resta = Math.max(0, this.tempoQ - (Date.now() - this.qInicio) / 1000);
        const el = $('t-questao');
        el.textContent = Math.ceil(resta);
        el.classList.toggle('urgente', resta <= 5);
        if (resta <= 0) this.responder(-1);
    },

    async responder(indice) {
        if (this.travado || this.qId === null) return;
        this.travado = true;
        const btns = [...$('alternativas').children];
        btns.forEach((b) => (b.disabled = true));
        const tempo = Math.min(this.tempoQ, (Date.now() - this.qInicio) / 1000);
        let proxima = null;
        try {
            const f = await Api.responder({
                codigoSala: this.codigo, jogadorId: this.jogadorId, questaoId: this.qId,
                indiceEscolhido: indice, tempoGastoSegundos: Math.round(tempo * 100) / 100,
            });
            if (btns[f.indiceCorreto]) btns[f.indiceCorreto].classList.add('ok');
            if (!f.correta && btns[indice]) btns[indice].classList.add('bad');
            const fb = $('feedback');
            fb.style.color = f.correta ? 'var(--ok)' : 'var(--erro)';
            fb.textContent = f.correta ? `Acertou! +${f.pontosGanhos} pts` : 'Errou!';
            proxima = f.proximaQuestao;
            await sleep(1000);
        } catch (e) {
            aviso(e.message);
        }
        this.travado = false;
        if (proxima) this.mostrarQuestao(proxima);
        else this.qId = null; // o próximo poll traz a questão atual
    },

    async finalizar(s) {
        this.finalizado = true;
        this.parar();
        let r = s.resultado;
        try { if (!r) r = await Api.resultado(this.codigo); } catch (e) { aviso(e.message); return; }
        this.renderResultado(r);
        Ranking.salvar(r);
        ir('resultado');
    },

    renderResultado(r) {
        $('res-titulo').textContent = r.vencedorId ? `${r.nomeVencedor} venceu!` : r.nomeVencedor;
        $('res-motivo').textContent = r.motivoVitoria || '';
        const box = $('res-placar');
        box.innerHTML = '';
        [r.jogador1, r.jogador2].forEach((j) => {
            if (!j) return;
            const d = mk('div', 'card_jogador' + (j.jogadorId === r.vencedorId ? ' vencedor' : ''));
            d.append(
                mk('span', '', j.nome),
                mk('b', '', `${j.pontuacaoFinal} pts`),
                mk('small', '', `${j.totalAcertos} acertos, ${j.totalErros} erros`),
                mk('small', '', `Aproveitamento: ${j.taxaAcertoPorcentagem}%`),
                mk('small', '', `Tempo médio: ${j.tempoMedioRespostaSegundos}s`)
            );
            box.append(d);
        });
    },
};