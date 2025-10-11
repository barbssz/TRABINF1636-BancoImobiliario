package model;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

class TestBancoImobiliarioModel {

    private BancoImobiliarioModel jogo;

    @Before
    public void setUp() {
        jogo = new BancoImobiliarioModel();
        jogo.adicionarJogador("A");
        jogo.adicionarJogador("B");
    }

    @Test
    public void testIniciarJogoComDoisJogadores() {
        jogo.iniciarJogo();
        List<Jogador> jogadoresAtivos = jogo.getJogadores();
        assertEquals(2, jogadoresAtivos.size());
    }

    @Test (expected = IllegalStateException.class)
    public void testErroIniciarComUmJogador() {
        BancoImobiliarioModel jogoSimples = new BancoImobiliarioModel();
        jogoSimples.adicionarJogador("A");
        jogoSimples.iniciarJogo(); // Deve lan�ar IllegalStateException
    }

    @Test
    public void testLancamentoDadosEntre2e12() {
        jogo.iniciarJogo();
        int r = jogo.lancarDados(); // A linha foi cortada aqui
        assertTrue("O valor do dado (" + r + ") deve ser maior ou igual a 2", r >= 2);
        assertTrue("O valor do dado (" + r + ") deve ser menor ou igual a 12", r <= 12);
    }
    
    @Test
    public void testDeslocarPiao() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);
        assertEquals(0, jogadorA.getPosicao()); // Verifica a posi��o inicial

        jogo.deslocarPiao(jogadorA, 5);
        // No seu tabuleiro atual, que tem 5 propriedades (�ndices 0 a 4), andar 5 casas a partir do 0 deve resultar em posi��o 0.
        assertEquals(0, jogadorA.getPosicao()); 
    }

    @Test
    public void testDeslocarPiaoComVoltaNoTabuleiro() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);
        int tamanhoTabuleiro = jogo.getTabuleiro().getPropriedades().size(); // No seu c�digo atual, � 5

        // Coloca o jogador perto do fim do tabuleiro para for�ar a volta
        jogadorA.setPosicao(tamanhoTabuleiro - 2); // Posi��o 3 (de 0-4)
        
        jogo.deslocarPiao(jogadorA, 4); // Anda 4 casas: da 3 -> 4 -> 0 -> 1 -> 2
        
        // A nova posi��o esperada � (3 + 4) % 5 = 7 % 5 = 2
        assertEquals(2, jogadorA.getPosicao());
    }
    
    @Test
    public void testPagarAluguelApenasComCasaConstruida() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0); // O inquilino
        Jogador jogadorB = jogo.getJogadores().get(1); // O propriet�rio

        int posicaoAlvo = 0;
        Terreno terreno = (Terreno) jogo.getTabuleiro().getPropriedade(posicaoAlvo);
        terreno.setDono(jogadorB);
        jogadorA.setPosicao(posicaoAlvo); // IMPORTANTE: Coloca o jogador A na propriedade

        int saldoInicialA = jogadorA.getSaldo();

        // --- Cen�rio 1: Pagar aluguel com 0 casas ---
        terreno.setNumCasas(0);
        jogo.pagarAluguel(jogadorA); // ATUALIZADO: N�o precisa mais passar o terreno

        assertEquals("Saldo n�o deve mudar pois n�o h� casas", saldoInicialA, jogadorA.getSaldo());

        // --- Cen�rio 2: Constr�i uma casa e paga aluguel de novo ---
        terreno.setNumCasas(1);
        jogo.pagarAluguel(jogadorA); // ATUALIZADO

        assertTrue("Saldo deve diminuir ap�s pagar aluguel com 1 casa", jogadorA.getSaldo() < saldoInicialA);
    }
    
    @Test
    public void testComprarPropriedadeNaPosicaoCorreta() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);

        // Move o jogador para a propriedade de �ndice 2 (Av. Paulista no seu c�digo)
        int posicaoAlvo = 2;
        jogadorA.setPosicao(posicaoAlvo);

        Propriedade propriedadeAlvo = jogo.getTabuleiro().getPropriedade(posicaoAlvo);
        int saldoInicial = jogadorA.getSaldo();

        // Jogador A tenta comprar a propriedade na posi��o em que est�
        jogo.comprarPropriedade(jogadorA);

        // Verifica se a compra foi bem-sucedida
        assertEquals("O dono da propriedade deve ser o jogador A", jogadorA, propriedadeAlvo.getDono());
        assertEquals("O saldo do jogador A deve ser descontado corretamente", saldoInicial - propriedadeAlvo.getPreco(), jogadorA.getSaldo());
    }

    @Test
    public void testNaoComprarPropriedadeQueJaTemDono() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);
        Jogador jogadorB = jogo.getJogadores().get(1);

        int posicaoAlvo = 1; // Posi��o da "Rua XV"
        jogadorA.setPosicao(posicaoAlvo);

        Propriedade propriedadeAlvo = jogo.getTabuleiro().getPropriedade(posicaoAlvo);
        propriedadeAlvo.setDono(jogadorB); // Jogador B j� � o dono

        int saldoInicialA = jogadorA.getSaldo();

        // Jogador A tenta comprar a propriedade que j� tem dono
        jogo.comprarPropriedade(jogadorA);

        // Verifica se nada mudou para o jogador A
        assertEquals("O dono ainda deve ser o jogador B", jogadorB, propriedadeAlvo.getDono());
        assertEquals("O saldo do jogador A n�o deve mudar", saldoInicialA, jogadorA.getSaldo());
    }
    
    @Test
    public void testConstruirCasaEmPropriedadePropria() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);

        int posicaoAlvo = 3; // Rua das Flores
        jogadorA.setPosicao(posicaoAlvo);

        Terreno terreno = (Terreno) jogo.getTabuleiro().getPropriedade(posicaoAlvo);
        terreno.setDono(jogadorA); // Jogador A � o dono

        int saldoInicial = jogadorA.getSaldo();
        int casasAntes = terreno.getNumCasas();

        // Jogador A constr�i uma casa no terreno onde est�
        jogo.construirCasa(jogadorA);

        assertEquals("O n�mero de casas deve aumentar em 1", casasAntes + 1, terreno.getNumCasas());
        assertEquals("O saldo do jogador deve diminuir o pre�o da casa", saldoInicial - terreno.getPrecoCasa(), jogadorA.getSaldo());
    }

    @Test
    public void testNaoConstruirCasaEmTerrenoDeOutro() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);
        Jogador jogadorB = jogo.getJogadores().get(1);

        int posicaoAlvo = 4; // Av. Central
        jogadorA.setPosicao(posicaoAlvo);

        Terreno terreno = (Terreno) jogo.getTabuleiro().getPropriedade(posicaoAlvo);
        terreno.setDono(jogadorB); // Jogador B � o dono

        int saldoInicialA = jogadorA.getSaldo();
        int casasAntes = terreno.getNumCasas();

        // Jogador A tenta construir em terreno alheio
        jogo.construirCasa(jogadorA);

        assertEquals("O n�mero de casas n�o deve mudar", casasAntes, terreno.getNumCasas());
        assertEquals("O saldo do jogador A n�o deve mudar", saldoInicialA, jogadorA.getSaldo());
    }
    
    @Test
    public void testEntrarESairDaPrisaoPagando() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);
        int saldoInicial = jogadorA.getSaldo();

        // Cen�rio 1: Entrar na pris�o
        assertFalse("Jogador n�o deve come�ar preso", jogadorA.estaPreso());
        jogo.enviarParaPrisao(jogadorA);
        assertTrue("Jogador deve estar preso ap�s ser enviado", jogadorA.estaPreso());

        // Cen�rio 2: Sair da pris�o pagando a fian�a de 50
        jogo.sairDaPrisao(jogadorA);
        assertFalse("Jogador deve estar livre ap�s pagar para sair", jogadorA.estaPreso());
        assertEquals("Saldo deve ser descontado em 50 para sair da pris�o", saldoInicial - 50, jogadorA.getSaldo());
    }

    @Test
    public void testNaoSairDaPrisaoSemSaldoSuficiente() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0);
        jogadorA.setSaldo(40); // Saldo insuficiente para pagar a fian�a de 50

        jogo.enviarParaPrisao(jogadorA);
        assertTrue("Jogador deve estar preso", jogadorA.estaPreso());

        // Tenta sair da pris�o sem dinheiro
        jogo.sairDaPrisao(jogadorA);

        assertTrue("Jogador deve continuar preso por falta de saldo", jogadorA.estaPreso());
        assertEquals("Saldo n�o deve mudar se o pagamento falhar", 40, jogadorA.getSaldo());
    }
    
    @Test
    public void testFalenciaAoNaoConseguirPagarAluguel() {
        jogo.iniciarJogo();
        Jogador jogadorA = jogo.getJogadores().get(0); // O que vai falir
        Jogador jogadorB = jogo.getJogadores().get(1); // O propriet�rio

        // Configura o cen�rio de fal�ncia
        int posicaoAlvo = 0;
        jogadorA.setPosicao(posicaoAlvo);
        jogadorA.setSaldo(100); // Saldo baixo e insuficiente

        Terreno terreno = (Terreno) jogo.getTabuleiro().getPropriedade(posicaoAlvo);
        terreno.setDono(jogadorB);
        terreno.setNumCasas(4); // For�a um aluguel alto (no seu caso, 100 * (4+1) = 500)

        int saldoInicialB = jogadorB.getSaldo();
        int jogadoresAntes = jogo.getJogadores().size();

        // A��o: Pagar aluguel que levar� � fal�ncia
        jogo.pagarAluguel(jogadorA);

        // Verifica��es
        assertEquals("N�mero de jogadores deve diminuir em 1", jogadoresAntes - 1, jogo.getJogadores().size());
        assertFalse("Jogador falido n�o deve mais estar na lista de jogadores", jogo.getJogadores().contains(jogadorA));
        assertEquals("Dono do terreno deve receber o saldo restante do jogador falido", saldoInicialB + 100, jogadorB.getSaldo());
    }
    
    
}
    
