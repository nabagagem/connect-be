Mural de ofertas: GET /api/v1/jobs -> pode filtrar por owner para listar "Meus trabalhos"

Publicar um trabalho: POST /api/v1/jobs

Detalhes de um trabalho: GET /api/v1/jobs/:jobId

Enviar proposta: POST /api/v1/bids, habilitado quando o usuario logado nao for o owner da proposta/trabalho

Fazer pergunta: POST /api/v1/threads

Acompanhar propostas: GET /api/v1/profile/{userId}/bids -> User id eh o usuario logado. Adicionar um filtro por job

Visualizar resposta da proposta: GET /api/v1/bids/{bidId} e GET /api/v1/bids/{bidId}/thread

Aceitar/recusar proposta: habilitar quando o usuario logado for o owner da proposta/trabalho

Ver todas as propostas deste trabalho: pode soh jogar pra tela de acompanhar propostas, filtrando por job, mas soh se o
usuario logado for o owner da proposta/trabalho

Ver perfil da trabalhadora: habilitar quando o usuario logado nao for o owner da proposta/trabalho. joga pra pagina de
perfil
