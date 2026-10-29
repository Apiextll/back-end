const API_BASE = location.port === '8080' ? '/api' : 'http://localhost:8080/api';

// Utilitários
const $ = (id) => document.getElementById(id);
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
const enc = encodeURIComponent;

function mk(tag, cls, texto) {
    const el = document.createElement(tag);
    if (cls) el.className = cls;
    if (texto !== undefined) el.textContent = texto;
    return el;
}

function ir(nome) {
    document.querySelectorAll('.tela').forEach((t) => t.classList.remove('ativa'));
    $('tela-' + nome).classList.add('ativa');
    document.dispatchEvent(new CustomEvent('tela', { detail: nome }));
}

let msgTimer;
function aviso(texto, tipo = 'erro') {
    const el = $('msg');
    el.textContent = texto;
    el.className = 'msg ' + tipo;
    el.hidden = false;
    clearTimeout(msgTimer);
    msgTimer = setTimeout(() => (el.hidden = true), 5000);
}

// Chamadas à API
async function req(path, opts = {}) {
    let r;
    try {
        r = await fetch(API_BASE + path, { headers: { 'Content-Type': 'application/json' }, ...opts });
    } catch {
        throw new Error('Não foi possível conectar ao servidor. Verifique se o back-end está rodando.');
    }
    const data = await r.json().catch(() => null);
    if (!r.ok) throw new Error((data && data.message) || `Erro ${r.status}`);
    return data;
}
const post = (path, body) => req(path, { method: 'POST', body: body ? JSON.stringify(body) : undefined });

const Api = {
    criarSala: (b) => post('/partidas', b),
    entrar: (b) => post('/partidas/entrar', b),
    pronto: (codigo, jogadorId) => post(`/partidas/${enc(codigo)}/pronto/${enc(jogadorId)}`),
    segundoTurno: (codigo) => post(`/partidas/${enc(codigo)}/iniciar-segundo-turno`),
    status: (codigo, jogadorId) =>
        req(`/partidas/${enc(codigo)}/status` + (jogadorId ? `?jogadorId=${enc(jogadorId)}` : '')),
    responder: (b) => post('/partidas/responder', b),
    resultado: (codigo) => req(`/partidas/${enc(codigo)}/resultado`),
    materias: () => req('/questoes/materias'),
    cadastrarQuestao: (b) => post('/questoes', b),
    reiniciarTorneio: () => post('/questoes/torneio/reiniciar'),
};