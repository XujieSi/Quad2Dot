import joeq.Compiler.Quad.*;
import joeq.Main.Helper;
import joeq.Class.*;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Iterator;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class PrintQuads
{
    final static String lineSep = "_$_";
    final static String dot_out_dir = "dot_out";

    static void printClass(jq_Reference r) {
        System.out.println("*** Class: " + r);
        if (r instanceof jq_Array)
            return;
        jq_Class c = (jq_Class) r;

	// create output directory
	String out_dir = dot_out_dir + File.separator +  c.getName();
	try{
	    new File(out_dir).mkdirs();
	} catch(Exception e) {
	    e.printStackTrace();
	    return;
	}


        for (jq_Method m : c.getDeclaredInstanceMethods()) {
            //printMethod(m);
	    printDotGraph(m, out_dir);
        }
        for (jq_Method m : c.getDeclaredStaticMethods()) {
            //printMethod(m);
	    printDotGraph(m, out_dir);
        }
    }

    static void printMethod(jq_Method m) {
        System.out.println("Method: " + m);
        if (!m.isAbstract()) {
            ControlFlowGraph cfg = m.getCFG();
            for (BasicBlock bb : cfg.reversePostOrder()) {
                for (Quad q : bb.getQuads()) {
                    int bci = q.getBCI();
                    System.out.println("\t" + bci + "#" + q.getID());
                }
            }
            System.out.println(cfg.fullDump());
        }
    }


    static String str(BasicBlock bb) {
	return "BB" + bb.getID();
    }


    static String dumpBB(BasicBlock bb) {
	// String lineSep = "\\l";
	// String lineSep = "_$_";

	StringBuffer sb = new StringBuffer();
        sb.append(bb.toString());
        sb.append("\t(in: ");

        Iterator<BasicBlock> bbi = bb.getPredecessors().iterator();
        if (!bbi.hasNext()) sb.append("<none>");
        else {
            sb.append(bbi.next().toString());
            while (bbi.hasNext()) {
                sb.append(", ");
                sb.append(bbi.next().toString());
            }
        }
        sb.append(", out: ");
        bbi = bb.getSuccessors().iterator();
        if (!bbi.hasNext()) sb.append("<none>");
        else {
            sb.append(bbi.next().toString());
            while (bbi.hasNext()) {
                sb.append(", ");
                sb.append(bbi.next().toString());
            }
        }
        sb.append(')');
        Iterator<ExceptionHandler> ehi = bb.getExceptionHandlers().iterator();
        if (ehi.hasNext()) {
            sb.append(lineSep+"\texception handlers: ");
            sb.append(ehi.next().toString());
            while (ehi.hasNext()) {
                sb.append(", ");
                sb.append(ehi.next().toString());
            }
        }
        sb.append(lineSep);
        Iterator<Quad> qi = bb.iterator();
        while (qi.hasNext()) {
            sb.append(qi.next().toString());
            sb.append(lineSep);
        }
        sb.append(lineSep);
        return sb.toString();

    }


    static void printDotGraph(jq_Method m, String out_dir) {
	if(m.isAbstract()) {
	    return;
	}

	StringBuffer sb = new StringBuffer();
	sb.append("digraph " + m.getName() + " {\n");

	ControlFlowGraph cfg = m.getCFG();

	for (BasicBlock bb : cfg.reversePostOrder()) {
	    sb.append( "  " + str(bb) + "[shape=box];\n"  );
	}
	sb.append("\n");

	for (BasicBlock bb : cfg.reversePostOrder()) {
	    for(BasicBlock pre: bb.getPredecessors()) {
		sb.append( "  " + str(pre) +  " -> " + str(bb) + ";\n" );
	    }
	}


	sb.append("\n");

	for (BasicBlock bb : cfg.reversePostOrder()) {
	    //String body = bb.fullDump().replace("\n", "\\l").replace("\"", "\\\"");
	    // String body = dumpBB(bb).replace("\n","\\\\n").replace("\"", "\\\\\\\"");

	    String t = dumpBB(bb).replace("\\","\\\\"); // one level evaluation
	    
	    // String t = dumpBB(bb).replace("\\","\\\\"); // two level evaluations
	    // String t = dumpBB(bb);
	    // int levels = 2;
	    // for(int i=0; i<levels; ++i) {
	    // 	t = t.replace("\\", "\\\\");
	    // }

	    String body = t.replace("\n","\\\\n").replace("\"","\\\"");

	    // System.out.println("bb.fullDump: ");
	    // System.out.println( bb.fullDump() );
	    // System.out.println("body:");
	    // System.out.println(body);

	    sb.append( "  " + str(bb) + "[label=\"" + body + "\"  ];\n"  );
	}
	
	sb.append("}\n");

	try{

	    PrintWriter writer = new PrintWriter(  out_dir + File.separator + m.getName() + ".dot" );

	    String res = sb.toString().replace(lineSep,"\\l");

	    writer.println( res );
	    writer.close();
	} catch(IOException e) {
	    e.printStackTrace();
	} 
    }

    public static void main(String[] args) {

	// ClassLoader cl = ClassLoader.getSystemClassLoader();

        // URL[] urls = ((URLClassLoader)cl).getURLs();

        // for(URL url: urls){
	//     System.out.println(url.getFile());
        // }

	// if(urls.length > 0) {
	//     return;
	// }

        jq_Class[] classes = new jq_Class[args.length];
        for (int i=0; i < classes.length; i++) {
            classes[i] = (jq_Class)Helper.load(args[i]);
	}

        for (int i=0; i < classes.length; i++) {
            System.out.println("Class: "+classes[i].getName());
            printClass(classes[i]);
            //Helper.runPass(classes[i], new PrintCFG());
        }
        
        System.out.println("Done");
    }
}
