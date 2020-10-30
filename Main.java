import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String prog =
                """
                        if(abc) {for(:) println(kto); if(sas);}
                        else { while(eshe chtoto) {chiki-briki} switch(a) {
                        case 1:
                        nu
                        kak
                        tam
                        break;
                        case 2:
                        s dengami
                        break;
                        default:
                        ti komu
                        zvonish
                        break;
                        }}
                        v pechat(finalochka)
                        """;
        prog = prog.replaceAll("[ ]*\\n[ ]*", "\n");

        int j = prog.indexOf("(");
        while (j != -1) {
            int start = j;
            int parentheses = 0;
            int semicolon = 0;
            int colon = 0;
            do {
                if (prog.charAt(j) == '(') parentheses++;
                if (prog.charAt(j) == ')') parentheses--;
                if (prog.charAt(j) == ';') semicolon++;
                if (prog.charAt(j) == ':') colon++;
                j++;
            } while (parentheses > 0);
            if (semicolon == 2 || colon == 1) {
                prog = prog.substring(0, start) + '`' + prog.substring(j);
            }
            j = prog.indexOf('(', start + 1);
        }


        String[] tokens = prog.split("(\\n)|(?<=[{};:])|(?=[{};:])");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
            if (!tokens[i].equals("")) {
                tokens[i] = tokens[i].replaceAll("[`]", "(;;)");
                list.add(tokens[i]);
            }
        }





        Graph graph = new Graph(list);
        Jilb jilb = new Jilb(graph);
        MGZ mgz = new MGZ(graph);
    }
}
