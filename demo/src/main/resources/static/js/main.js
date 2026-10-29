// Ranking local (localStorage).
const Ranking = {
    KEY: 'typewars_ranking',
    ler() {
        try { return JSON.parse(localStorage.getItem(this.KEY)) || []; } catch { return []; }
    },
    salvar(r) {
        const lista = this.ler();
        if (lista.some((x) => x.codigo === r.codigoPartida && x.data === r.finalizadaEm)) return;
        [r.jogador1, r.jogador2].forEach((j) => {
            if (j) lista.push({
                codigo: r.codigoPartida, data: r.finalizadaEm, nome: j.nome,
                pontos: j.pontuacaoFinal, acertos: j.totalAcertos, tempo: j.tempoMedioRespostaSegundos,
            });
        });
        try { localStorage.setItem(this.KEY, JSON.stringify(lista)); } catch {}
    },
    limpar() {
        try { localStorage.removeItem(this.KEY); } catch {}
    },
    render() {
        const corpo = document.querySelector('#tabela-rank tbody');
        corpo.innerHTML = '';
        const lista = this.ler().sort((a, b) => b.pontos - a.pontos || b.acertos - a.acertos || a.tempo - b.tempo);
        if (!lista.length) {
            const tr = mk('tr');
            const td = mk('td', '', 'Nenhuma partida finalizada ainda.');
            td.colSpan = 5;
            tr.append(td);
            corpo.append(tr);
            return;
        }
        lista.slice(0, 15).forEach((x, i) => {
            const tr = mk('tr');
            [i + 1, x.nome, x.pontos, x.acertos, `${x.tempo}s`].forEach((v) => tr.append(mk('td', '', String(v))));
            corpo.append(tr);
        });
    },
};

// Matérias vindas da API (para criar sala e cadastrar questão)
let materiasOk = false;
async function carregarMaterias() {
    if (materiasOk) return;
    try {
        const ms = await Api.materias();
        ['c_materia', 'q_materia'].forEach((id) => {
            const sel = $(id);
            sel.innerHTML = '';
            ms.forEach((m) => sel.add(new Option(m.nome, m.codigo)));
        });
        materiasOk = true;
    } catch (e) {
        aviso(e.message);
    }
}

document.addEventListener('tela', (e) => {
    if (e.detail === 'ranking') Ranking.render();
    if (e.detail === 'criar' || e.detail === 'admin') carregarMaterias();
});

document.querySelectorAll('[data-ir]').forEach((b) => b.addEventListener('click', () => ir(b.dataset.ir)));

$('c_modo').addEventListener('change', (e) => {
    $('nomes-local').hidden = e.target.value !== 'TURNO_LOCAL_1PC';
});

// Entrar na sala
$('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    const codigo = $('codigo_sala').value.trim().toUpperCase();
    try {
        const j = await Api.entrar({ codigo, senha: $('senha_sala').value, nomeJogador: $('username').value.trim() });
        Jogo.iniciar(codigo, j.id, false);
    } catch (err) {
        aviso(err.message);
    }
});

// Criar sala
$('form-criar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const codigo = $('c_codigo').value.trim().toUpperCase();
    const local = $('c_modo').value === 'TURNO_LOCAL_1PC';
    try {
        const s = await Api.criarSala({
            codigo, senha: $('c_senha').value, modoJogo: $('c_modo').value, materia: $('c_materia').value,
            nomeJogador1: local ? $('c_n1').value.trim() || 'Jogador 1' : null,
            nomeJogador2: local ? $('c_n2').value.trim() || 'Jogador 2' : null,
        });
        if (local) {
            Jogo.iniciar(codigo, s.jogador1.id, true);
        } else {
            $('codigo_sala').value = codigo;
            $('senha_sala').value = $('c_senha').value;
            ir('login');
            aviso(`Sala ${codigo} criada. Cada jogador entra pelo seu computador.`, 'ok');
        }
    } catch (err) {
        aviso(err.message);
    }
});

// Pronto / sair / segundo turno
$('btn-pronto').addEventListener('click', async () => {
    try { await Api.pronto(Jogo.codigo, Jogo.jogadorId); Jogo.poll(); } catch (err) { aviso(err.message); }
});
$('btn-sair').addEventListener('click', () => { Jogo.parar(); ir('login'); });
$('btn-segundo-turno').addEventListener('click', async () => {
    try {
        const s = await Api.segundoTurno(Jogo.codigo);
        Jogo.qId = null;
        Jogo.travado = false;
        Jogo.render(s);
    } catch (err) {
        aviso(err.message);
    }
});

// Ranking e administração
$('btn-limpar-rank').addEventListener('click', () => {
    if (confirm('Apagar o ranking salvo neste computador?')) { Ranking.limpar(); Ranking.render(); }
});

$('form-questao').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const r = await Api.cadastrarQuestao({
            enunciado: $('q_enunciado').value.trim(), materia: $('q_materia').value, dificuldade: $('q_dif').value,
            alternativas: [...document.querySelectorAll('.q_alt')].map((i) => i.value.trim()),
            indiceCorreto: Number($('q_correta').value),
        });
        aviso(r.mensagem, 'ok');
        e.target.reset();
    } catch (err) {
        aviso(err.message);
    }
});

$('btn-reiniciar').addEventListener('click', async () => {
    if (!confirm('Reiniciar o torneio? O histórico de questões usadas será zerado.')) return;
    try { aviso((await Api.reiniciarTorneio()).mensagem, 'ok'); } catch (err) { aviso(err.message); }
});