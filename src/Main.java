import pt.iscte.greyditor.Greyditor;
import pt.iscte.greyditor.Editor;
import pt.iscte.greyditor.Selection;


public class Main {

    public static void main(String[] args) { // static - vive dentro da classe, não do objeto

        Main m = new Main(); // !!! objeto - é o boneco feito com o molde da class main

        Greyditor config = new Greyditor("Monalisa");
        config.addFilter("brighten", m::brighten, 0, 255);
        config.addFilter("darken", m::darken, 0, 255);
        config.addFilter("contrast", m::contrast, 0, 255);

        config.addEffect("mirror horizontal", m::mirrorHorizontal);
        config.addEffect("mirror vertical", m::mirrorVertical);
        config.addEffect("grain", m::grain, 0, 255);
        config.addEffect("margem", m::margem, 0, 255);
        config.addEffect("vinheta", m::vinheta, 0, 255);

        config.addOperation("crop", m::crop);
        config.addOperation("expand", m::expand);
        config.addOperation("posterizar", (pt.iscte.greyditor.OperationSimple) m::posterizar);
        config.addOperation("rodar", m::rodar);
        config.addOperation("blur", m::blur);

        config.addEffect("antigo", m::antigo);
        config.addEffect("retro", m::retro);

        config.addOperation("copiar", m::copiar);
        config.addOperation("cut", m::cut);
        config.addOperation("paste", m::paste);
        config.addOperation("undo", (pt.iscte.greyditor.OperationSimple) m::undo);


        // abre a interface e a imagem
        config.open("monalisa.jpg");
    }

    // Clarear
    int brighten(int value, int intensity) {
        int result = value + intensity;
        if (result > 255) {
            return 255;
        }
        return result;
    }

    // Escurecer
    int darken(int value, int intensity) {
        int result = value - intensity;
        if (result < 0) {
            return 0;
        }
        return result;
    }

    /*int darken0(int value, int intensity) {
        return Math.max(0, value - intensity);
    }
    */

    // Contraste
    int contrast(int value, int intensity) {


        if (value >= 128) { // meio termo nas cores - INTENSIFICA BRANCOS
            int result = value + intensity;
            if (result > 255)
                result = 255;
            return result;
        } else {
            int result = value - intensity;
            if (result < 0)
                result = 0; // INTENSIFICA PRETOS
            return result;
        }
    }
    /*
    int contrast(int value, int int){
        return (value<128) ? Math.max(0, value - intensity):Math.min(value + intensity, 255);
    }
    */

    void mirrorHorizontal (int[][] image) { // não retorna nada, altera a imagem diretament

        int linhas = image.length;
        int colunas = image[0].length;

        for (int i = 0; i < linhas; i++) { // percorre cada linha
            for (int x = 0; x < colunas/2; x++) {   // percorre só metade da linha - pq cada troca afeta 2 pixels

                int temp = image[i][x];               // guardar o valor original do lado esquerdo
                image[i][x] = image[i][colunas - 1 - x]; // copiar o valor da direita para a esquerda
                image[i][colunas - 1 - x] = temp;     // meter o valor original da esquerda no lado direito
            }
        }
    }

    void mirrorVertical (int[][] image) {


        int linhas = image.length;
        int colunas = image[0].length;

        for (int i = 0; i < linhas/2; i++) {
            for (int x = 0; x < colunas; x++) {

                int  temp = image[i][x];
                image[i][x] = image[linhas - 1 - i][x];
                image[linhas - 1 - i][x] = temp;
            }
        }
    }

    void grain(int[][] image, int intensidade) {
        int linhas  = image.length;
        int colunas = image[0].length;

        for (int y = 0; y < linhas; y++) { // percorre linhas
            for (int x = 0; x < colunas; x++) { // percorre as colunas - ANALISA A MATRIZ INTEIRA

                int ruido = (int)(Math.random() * (intensidade * 2 + 1)) - intensidade;
                int resultado = image[y][x] + ruido;

                if (resultado > 255) resultado = 255; // limitar no max para 255
                if (resultado < 0)   resultado = 0; // limitar no min a 0 - problema dos vermelhos resolvido

                image[y][x] = resultado;
            }
        }
    }

    void margem(int[][] image, int intensidade) {
        int linhas  = image.length;
        int colunas = image[0].length;

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {

                boolean topo = y < intensidade;
                boolean fundo = y >= linhas - intensidade;
                boolean esquerda = x < intensidade;
                boolean direito = x >= colunas - intensidade;

                if (topo || fundo || esquerda || direito) {
                    image[y][x] = 255;
                }
            }
        }
    }

    void vinheta(int[][] image, int intensidade) { //
        int linhas  = image.length;
        int colunas = image[0].length;

        int centroX = colunas / 2;
        int centroY = linhas / 2;

        int maxDistancia = centroX + centroY; // referência para o pixel - longe ou perto do centro

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {

                int dx = Math.abs(x - centroX);   // quão longe está horizontalmente - módulo
                int dy = Math.abs(y - centroY);   // quão longe está verticalmente - módulo
                int dist = dx + dy;               // distância total ao centro

                double fator = (double) dist / maxDistancia; // 0 no centro, 1 nos cantos

                int tomOriginal = image[y][x]; // perceber o tom do pixel
                int novoTom = (int) (tomOriginal - intensidade * fator); // extrair o tom para 0 e 1

                if (novoTom < 0) novoTom = 0;

                image[y][x] = novoTom;
            }
        }
    }

    int[][] crop(int[][] imagem, Editor editor) {

        Selection s = editor.getSelection(); // Verifica se há área selecionada
        if (s == null) {
            editor.message("nehuma area selecionada.");
            return null;
        }

        guardarAntes(imagem);

        int x = s.x();
        int y = s.y();
        int w = s.width();
        int h = s.height();

        // nova imagem com o tamanho selecionado
        int[][] nova = new int[h][w];
        // copiar os pixeis da área selecionada
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                nova[i][j] = imagem[y + i][x + j];
            }
        }

        return nova; // Greyditor substitui a imagem atual por esta
    }


    int[][] expand(int[][] imagem, Editor editor) {

        guardarAntes(imagem);

        int linhas = imagem.length;
        int colunas = imagem[0].length;


        int novaAltura = editor.getInteger("Indica a nova altura (>= " + linhas + "):");
        int novaLargura = editor.getInteger("Indica a nova largura (>= " + colunas + "):");

        // condição para só expandir
        if (novaAltura < linhas || novaLargura < colunas) {
            editor.message("a nova dimensão tem de ser MAIOR que a atual");
            return null;
        }

        int[][] nova = new int[novaAltura][novaLargura];

        // copiar imagem original para o canto superior esquerdo
        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {
                nova[y][x] = imagem[y][x];
            }
        }
        return nova; // substitui a imagem pela nova
    }

    int[][] posterizar(int[][] image) {
        guardarAntes(image);

        int linhas = image.length;
        int colunas = image[0].length;

        int cores = 4; // número fixo de tons - 4 tons 0-64 , 64-128, ....
        int intervalo = 256 / cores;

        int[][] nova = new int[linhas][colunas];

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {

                int tomOriginal = image[y][x]; // valor do pixel
                int novoTom = (tomOriginal / intervalo) * intervalo;

                if (novoTom > 255) novoTom = 255;
                if (novoTom < 0) novoTom = 0;

                nova[y][x] = novoTom;
            }
        }

        return nova;
    }

    int[][] rodar(int[][] image) {

        int linhas = image.length;
        int colunas = image[0].length;

        // A imagem roda -  troca altura por largura!
        int[][] nova = new int[colunas][linhas];

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {
                nova[x][linhas - 1 - y] = image[y][x]; // x passa a ser linhas e invertemos a direção vertical (trocamos linhas com colunas)
            }
        }

        return nova;
    }

    int[][] blur(int[][] image) {
        guardarAntes(image);
        int linhas  = image.length;
        int colunas = image[0].length;

        int[][] nova = new int[linhas][colunas];

        int r = 1; // raio do blur

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {

                int soma = 0;
                int contador = 0;

                // percorremos dentro do raio r os pixels
                for (int dy = -r; dy <= r; dy++) {
                    for (int dx = -r; dx <= r; dx++) {

                        int ny = y + dy;
                        int nx = x + dx;

                        // verificar limites da matriz para não sair da imagem
                        if (ny >= 0 && ny < linhas && nx >= 0 && nx < colunas) {
                            soma += image[ny][nx];
                            contador++;
                        }
                    }
                }

                nova[y][x] = soma / contador; // média dos tons vizinhos
            }
        }
        return nova;
    }

    void antigo(int[][] image) {
        guardarAntes(image);
        int intensidade = 20;

        grain(image, intensidade);       // primeiro grão
        vinheta(image, intensidade);     // depois vignette
        margem(image, intensidade);      // e por fim a margem
    }

    int[][] retro(int[][] image) {
        guardarAntes(image);
        int linhas  = image.length;
        int colunas = image[0].length;

        // contraste mais forte - aplicamos desta forma por ser um filtro - altera pixel individualmente
        int intensidade = 50;
        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {
                image[y][x] = contrast(image[y][x], intensidade);
            }
        }
        int[][] nova = blur(image); // meter blur
        vinheta(nova, 30); // meter a vinheta

        return nova; // devolve a imagem transformada
    }

    // variaveis globais

    int[][] clipboard = null; // guardar o copiado/colado
    boolean foiCortado = false; // cut = true, copy = false
    int[][] ultimaImagem = null; // cópia da imagem antes da ação

    void guardarAntes(int[][] imagem) { // "cópia" da imagem antes de alterar

        ultimaImagem = new int[imagem.length][imagem[0].length];

        for (int y = 0; y < imagem.length; y++) // copiar o conteúdo
            for (int x = 0; x < imagem[0].length; x++)
                ultimaImagem[y][x] = imagem[y][x];
    }


    // guarda a seleção na clipboard SEM alterar a imagem
    int[][] copiar(int[][] imagem, Editor editor) {

        Selection s = editor.getSelection(); // area selecionada
        if (s == null) return null;

        int x = s.x(), y = s.y(), w = s.width(), h = s.height(); // largura e altura

        clipboard = new int[h][w];

        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                clipboard[i][j] = imagem[y + i][x + j]; // copia pixel a pixel da imagem para a clipboard

        editor.message("area copiada");
        return null;
    }

    // copia + pinta a seleção a branco
    int[][] cut(int[][] imagem, Editor editor) {

        Selection s = editor.getSelection();
        if (s == null) return null;

        guardarAntes(imagem);

        int x = s.x(), y = s.y(), w = s.width(), h = s.height();

        clipboard = new int[h][w];
        foiCortado = true;

        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++) {
                clipboard[i][j] = imagem[y + i][x + j]; // imagem selecionada
                imagem[y + i][x + j] = 255; // substitui a imagem por branco
            }
        return null;
    }



    // cola clipboard na seleção ou no topo-esquerda por pre-definição
    int[][] paste(int[][] imagem, Editor editor) {

        if (clipboard == null) {
            return null;
        }

        guardarAntes(imagem);

        Selection s = editor.getSelection();
        int x = (s == null ? 0 : s.x()); //se não marcar nenhum sítio mete no canto superior esquerdo (0,0)
        int y = (s == null ? 0 : s.y());

        for (int i = 0; i < clipboard.length; i++)
            for (int j = 0; j < clipboard[i].length; j++)
                if (y + i < imagem.length && x + j < imagem[0].length) // pixel nao sai fora da imagem
                    imagem[y + i][x + j] = clipboard[i][j]; // i j para centrlizar

        if (foiCortado) {
            clipboard = null;
            foiCortado = false; // só colar 1 vez depois do CUT
        }

        return null;
    }


    // voltar atrás 1x - simples
    int[][] undo(int[][] imagem) {

        if (ultimaImagem == null)
            return null; // nada guardado → nada a desfazer

        int[][] copia = new int[ultimaImagem.length][ultimaImagem[0].length];

        for (int y = 0; y < ultimaImagem.length; y++) // copia completa da imagem
            for (int x = 0; x < ultimaImagem[0].length; x++)
                copia[y][x] = ultimaImagem[y][x];

        ultimaImagem = null;

            return copia; // greyditor substitui a imagem por esta cópia
    }

}
