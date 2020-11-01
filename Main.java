import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String prog =
                """
if(abc) {for(:) println(kto); if(sas) do {raz; dva;} while(chtoto);} // ;if(){}
else { while(eshe chtoto) {chiki-briki} switch(a) {
case 1:
nu
/*
tipa ;
coment
*/
kak
tam
break;
case 2:
s dengami " tipa literal ;for(){}"
break;
default:
ti komu
zvonish
break;
}}
v pechat(finalochka)



                                                                                                            """;


        Tokenizer tokenizer = new Tokenizer(prog);
        Graph graph = new Graph(tokenizer.getTokens());
        Jilb jilb = new Jilb(graph);
        MGZ mgz = new MGZ(graph);
        System.out.println(jilb.operators() + "\n" + jilb.branching() + "\n" + jilb.relativeComplexity() + "\n" + jilb.nesting() + "\n\n" + mgz.absoluteComplexity() + "\n" + mgz.relativeComplexity());
    }
}
