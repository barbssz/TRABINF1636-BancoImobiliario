package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe principal que representa o modelo do jogo de Banco Imobiliário.
 * Gerencia jogadores, tabuleiro, regras de movimentação, compra, aluguel e construção.
 */
public class BancoImobiliarioModel {
    /**
     * Lista de jogadores participantes da partida.
     */
    private List<Jogador> jogadores;

    /**
     * Tabuleiro do jogo, contendo todas as propriedades.
     */
    private Tabuleiro tabuleiro;

    /**
     * Índice do jogador da vez (turno atual).
     */
    private int jogadorDaVez;

    /**
     * Construtor do modelo do jogo.
     * Inicializa a lista de jogadores, o tabuleiro e define o primeiro jogador da vez.
     */
    public BancoImobiliarioModel() {
        jogadores = new ArrayList<>();
        tabuleiro = new Tabuleiro();
        jogadorDaVez = 0;
    }

    /**
     * Adiciona um novo jogador à partida.
     * Limite máximo de 6 jogadores.
     * @param nome Nome do jogador.
     */
    public void adicionarJogador(String nome) {
        if(jogadores.size() >= 6) 
            return;
        jogadores.add(new Jogador(nome));
    }

    /**
     * Inicia o jogo, atribuindo saldo inicial aos jogadores.
     * Exige pelo menos dois jogadores.
     */
    public void iniciarJogo() {
        if(jogadores.size() < 2) throw new IllegalStateException("Sao necessarios pelo menos dois jogadores para iniciar o jogo.");
        for(Jogador j : jogadores) j.setSaldo(4000); // saldo inicial equivalente às regras
    }

    /**
     * Simula o lançamento de dois dados de seis faces.
     * @return Soma dos valores dos dois dados (2 a 12).
     */
    public int lancarDados() {
        Random rand = new Random();
        return rand.nextInt(6) + 1 + rand.nextInt(6) + 1; // 2 a 12
    }
    
    /**
     * Move o peão do jogador pelo tabuleiro.
     * @param jogador Jogador a ser movimentado.
     * @param casas Número de casas a avançar.
     */
    public void deslocarPiao(Jogador jogador, int casas) {
        int tamanhoTabuleiro = tabuleiro.getPropriedades().size();
        int novaPosicao = (jogador.getPosicao() + casas) % tamanhoTabuleiro;
        jogador.setPosicao(novaPosicao);
    }

    /**
     * Permite ao jogador comprar a propriedade onde está posicionado, se possível.
     * Verifica saldo, existência e se não há dono.
     * @param jogador Jogador que deseja comprar a propriedade.
     */
    public void comprarPropriedade(Jogador jogador) {
        Propriedade prop = tabuleiro.getPropriedade(jogador.getPosicao());
        if (prop != null && prop.getDono() == null) {
            if (jogador.getSaldo() >= prop.getPreco()) {
                jogador.setSaldo(jogador.getSaldo() - prop.getPreco());
                prop.setDono(jogador);
            }
        }
    }

    /**
     * Permite ao jogador construir uma casa em um terreno que possua e tenha saldo suficiente.
     * @param jogador Jogador que deseja construir a casa.
     */
    public void construirCasa(Jogador jogador) {
        Propriedade prop = tabuleiro.getPropriedade(jogador.getPosicao());
        if (prop instanceof Terreno) {
            Terreno terreno = (Terreno) prop;
            if (terreno.getDono() == jogador) {
                if (jogador.getSaldo() >= terreno.getPrecoCasa()) {
                    jogador.setSaldo(jogador.getSaldo() - terreno.getPrecoCasa());
                    terreno.construirCasa();
                }
            }
        }
    }

    /**
     * Realiza o pagamento de aluguel ao dono da propriedade onde o jogador está.
     * Remove o jogador em caso de falência.
     * @param pagador Jogador que deve pagar o aluguel.
     */
    public void pagarAluguel(Jogador pagador) {
        Propriedade prop = tabuleiro.getPropriedade(pagador.getPosicao());
        if (prop != null && prop.getDono() != null && prop.getDono() != pagador) {
            int aluguel = prop.calcularAluguel();
            if (aluguel > 0) {
                if (pagador.getSaldo() >= aluguel) {
                    pagador.setSaldo(pagador.getSaldo() - aluguel);
                    prop.getDono().setSaldo(prop.getDono().getSaldo() + aluguel);
                } else {
                    prop.getDono().setSaldo(prop.getDono().getSaldo() + pagador.getSaldo());
                    pagador.setSaldo(0);
                    jogadores.remove(pagador);
                }
            }
        }
    }

    /**
     * Envia o jogador para a prisão.
     * @param j Jogador a ser preso.
     */
    public void enviarParaPrisao(Jogador j) {
        j.setPreso(true);
    }

    /**
     * Permite ao jogador sair da prisão pagando uma taxa.
     * @param j Jogador que deseja sair da prisão.
     */
    public void sairDaPrisao(Jogador j) {
        if(j.getSaldo() >= 50) {
            j.setSaldo(j.getSaldo() - 50);
            j.setPreso(false);
        }
    }

    /**
     * Retorna a lista de jogadores da partida.
     * @return Lista de jogadores.
     */
    public List<Jogador> getJogadores() {
        return jogadores;
    }

    /**
     * Retorna o tabuleiro do jogo.
     * @return Tabuleiro.
     */
    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }
}
