package Model;
/** Criamos uma classe abstrata chamada "Casa", que representa
 * um espaço no tabuleiro do jogo. Ela não poderá ser instanciada porque
 * será herdada por outras classes, como Propriedade, Prisão, Sorte/Revés etc.
 * 
*/
abstract class Casa{
    int posicao;
    String propriedade;

    public Casa(int posicao, String propriedade){
        this.posicao = posicao;
        this.propriedade = propriedade;
    }
}
