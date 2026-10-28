# Diretrizes de Desenvolvimento e Padrões de Código

Este documento define os padrões obrigatórios de arquitetura, escrita de código e boas práticas para o projeto Kyofuse. Todas as alterações, refatorações e novas funcionalidades devem seguir rigorosamente estas diretrizes.

---

## 1. Backend (Java / Spring Boot)

### 1.1. Arquitetura e Separação de Responsabilidades (SOLID / Clean Code)
- **Princípio da Responsabilidade Única (SRP)**:
  - Nenhuma classe `Service` deve acumular validações de regras de negócio, conversões/mapeamentos, buscas repetitivas no banco ou lógica secundária/complementar.
- **Mappers (`*Mapper`)**:
  - **Sempre** utilizar classes `@Component` dedicadas para mapear entre Entidades e DTOs/Requests/Responses.
  - **Nunca** instanciar ou construir entidades ou DTOs usando `.builder().build()` ou construtores diretamente dentro da `Service`.
- **Validators (`*Validator`)**:
  - **Sempre** extrair validações de regras de negócio para classes `@Component` dedicadas (ex: `UserValidator`, `TeamInviteValidator`).
  - **Nunca** deixar blocos `if (...) throw new ...` explícitos dentro dos métodos da `Service`. O método da service deve apenas chamar o validador correspondente.
- **Finders (`*Finder`)**:
  - **Sempre** criar e utilizar classes `@Component` `*Finder` para consultas de entidades (por ID, slug, email, etc.).
  - O `Finder` deve encapsular a busca no repositório e lançar `ResourceNotFoundException` quando o registro não existir.
  - **Evitar** injeção e chamadas diretas de `Repository.findById(...)` repetitivas dentro de services.
- **Services de Suporte (`*Support` / Classes Complementares)**:
  - Criar services auxiliares para lógica complementar (ex: envio de notificações, auditoria, sincronização externa, contadores de reações).
  - **Evitar ao máximo métodos privados dentro das services**: se a lógica não for estritamente interna e trivial, ela deve ser extraída para uma service de suporte ou componente auxiliar.
- **Controllers Enxutos (Thin Controllers)**:
  - Controllers devem apenas receber os dados da requisição com validação (`@Valid`), repassar a chamada para a service correspondente e retornar o `ResponseEntity`.
  - Nenhuma regra de negócio ou lógica condicional deve residir no Controller.

### 1.2. Padrões de Linguagem e Boas Práticas (Java)
- **DTOs com Java Records**:
  - **Sempre** utilizar `record` para DTOs, Requests e Responses imutáveis.
- **Injeção de Dependências**:
  - **Sempre** utilizar `@RequiredArgsConstructor` do Lombok com atributos `private final`.
  - **Nunca** utilizar `@Autowired` em campos.
- **Imports no Topo**:
  - **Nunca** utilizar *inline imports* (ex: `java.util.List<String>` no meio do código). Todos os imports devem estar organizados no topo da classe.
  - Não deixar imports não utilizados ou com curingas (`*`).
- **Exceções Padronizadas**:
  - **Nunca** lançar `RuntimeException` genérica.
  - Utilizar sempre exceções de domínio padronizadas de acordo com o cenário:
    - `ResourceNotFoundException`: Quando um recurso não for encontrado.
    - `BadRequestException`: Regras de negócio violadas ou parâmetros inválidos.
    - `ConflictException`: Conflito de estado ou duplicidade de registros.
    - `ForbiddenException`: Usuário sem permissão para executar a ação.
    - `UnauthorizedException`: Falha de autenticação ou token inválido.
- **Transações (`@Transactional`)**:
  - Utilizar `@Transactional(readOnly = true)` para métodos ou classes exclusivas de leitura.
  - Utilizar `@Transactional` para operações de escrita/mutação que exigem atomicidade.
- **Logging**:
  - Utilizar `@Slf4j` do Lombok para logs estruturados nos pontos relevantes de execução.

---

## 2. Frontend (Angular / TypeScript)

### 2.1. Arquitetura de Componentes
- **Standalone Components**:
  - Todos os componentes, diretivas e pipes devem ser standalone (`standalone: true`).
- **Injeção com `inject()`**:
  - Utilizar a função `inject()` para injeção de dependências em vez de injeção no construtor.
- **Reatividade com Signals**:
  - Priorizar o uso de **Angular Signals** (`signal()`, `computed()`, `effect()`) para gerenciamento de estado reativo nos componentes.
- **Separação de Arquivos**:
  - Manter sempre arquivos separados para lógica (`.ts`), template (`.html`) e estilo (`.scss`/`.css`).
  - Manter os templates limpos: evitar lógica complexa diretamente no HTML. Usar `computed` signals ou métodos auxiliares no `.ts`.

### 2.2. Tipagem Estrita e Modelagem
- **Proibido o uso de `any`**:
  - **Sempre** definir interfaces e tipos TypeScript explícitos para todas as entidades, DTOs, respostas de API e payloads.
  - Criar e organizar modelos na pasta `models/` correspondente ao domínio.
- **Serviços HTTP**:
  - Centralizar chamadas de API em serviços dedicados em `core/services/`.
  - Componentes nunca devem chamar `HttpClient` diretamente.

### 2.3. Gestão de Observables e Ciclo de Vida
- Evitar vazamentos de memória (memory leaks):
  - Utilizar `takeUntilDestroyed()`, `firstValueFrom` ou o pipe `async` no template para gerenciar inscrições de Observables.

### 2.4. Internacionalização (i18n)
- Toda string visível ao usuário deve utilizar o sistema de tradução configurado no projeto (chaves em `i18n`), evitando textos hardcoded no template.

---

## 3. Git e Padrões de Commit

- Seguir o padrão de **Conventional Commits** em português:
  - `feat(modulo): descricao da nova funcionalidade`
  - `refactor(modulo): descricao da refatoracao`
  - `fix(modulo): correcao de bug`
  - `test(modulo): adicao ou ajuste de testes`
  - `docs(modulo): alteracao em documentacao`
  - `style(modulo): ajustes de formatacao ou estilo sem impacto logico`
- Nomes de branches claros seguindo o formato: `tipo/descricao-kebab-case` (ex: `feat/team-join-requests`, `refactor/services-cleanup`).
