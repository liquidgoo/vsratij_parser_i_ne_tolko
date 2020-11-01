import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String prog =
                """
	int x = 1;
	
	switch(x){
		case 1:
			x++;
			break;
		case 2:
			x++;
break;
		case 3:
			x++;
break;
		case 4:
			while (x != 10) x++;
break;
		case 10:
			x += 15;
break;
		case 25:
			break;
	}
	
	System.out.println(x);

                        """;


        Tokenizer tokenizer = new Tokenizer(prog);
        Graph graph = new Graph(tokenizer.getTokens());
        Jilb jilb = new Jilb(graph);
        MGZ mgz = new MGZ(graph);
        System.out.println(jilb.operators() + "\n" + jilb.branching() + "\n" + jilb.relativeComplexity() + "\n" + jilb.nesting() + "\n\n" + mgz.absoluteComplexity() + "\n" + mgz.relativeComplexity());
    }
}
