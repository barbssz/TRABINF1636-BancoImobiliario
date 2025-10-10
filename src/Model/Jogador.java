package Model;
import java.util.*;

enum Cor{
     VERMELHO, AZUL, LARANJA, AMARELO, ROXO, CINZA;
}

class Jogador {
    /*Propriedades de objeto*/
    private int qtdFugaPrisao;
    private boolean estaPreso;
    private float dinheiro;
    private Cor cor;
    private String nome;

    /*Construtor */
    public Jogador(Cor cor, String nome) {
        this.cor = cor;
        this.estaPreso = false;
        this.dinheiro = 4000;
        this.qtdFugaPrisao = 0;
        this.nome = nome;
    }
    
    /*Função para gerar números entre min e max, incluindo os limites */
    public int generateRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    

    /*Função que retorna um array com 2 elementos, representando o resultado dos lancamentos de cada dado */
    public int[] lancarDados(){
        int dados[];
        dados = new int[2];
        dados[0] = generateRandomInt(0, 6);
        dados[1] = generateRandomInt(0, 6);
        return dados;
    }
    
}