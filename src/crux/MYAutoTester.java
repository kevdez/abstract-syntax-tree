package crux;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class MYAutoTester {
	
	public static final int PASS = 0;
	public static final int FAIL = 1;
	public static final int NOT_ACCESSABLE = 2;
	public static final int IO_ERROR = 3;
	public static final int SLEEP_ERROR = 4;
	

	public static int testPrivate(){
		String inputFilename = "tests/test02.crx";
		String outputFilename = "tests/test02.rea";
		String expectedFilename = "tests/test02.out";
		
		Scanner s = null;
        try {
	        s = new Scanner(new FileReader(inputFilename));
        } catch (IOException e) {
            e.printStackTrace();
            return NOT_ACCESSABLE;
        }
        
        Parser p = new Parser(s);
        ast.Command syntaxTree = p.parse();
		try {
			PrintStream outputStream = new PrintStream(outputFilename);
			if (p.hasError()) {
				outputStream.println("Error parsing file.");
				outputStream.println(p.errorReport());
				outputStream.close();
                //System.exit(-3);
            } else {
            	ast.PrettyPrinter pp = new ast.PrettyPrinter();
                syntaxTree.accept(pp);
                outputStream.println(pp.toString());
                outputStream.close();
            }
			
		} catch (IOException e) {
            System.err.println("Error opening output file: \"" + outputFilename + "\"");
			e.printStackTrace();
			return IO_ERROR;
		}
		
		BufferedReader bufferedexpected;
		BufferedReader bufferedoutput;

		String lineExpected;
		String lineOutput;
		
		try {
			bufferedexpected = new BufferedReader(new FileReader(expectedFilename));
			bufferedoutput = new BufferedReader(new FileReader(outputFilename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return IO_ERROR;
		}

		int result = PASS;

		try {
			while ((lineExpected = bufferedexpected.readLine()) != null) {
				lineOutput = bufferedoutput.readLine();
				if (lineOutput == null) {
					result = FAIL;
					break;
				}
				lineExpected = lineExpected.replaceAll("\\s+$", "");
				lineOutput = lineOutput.replaceAll("\\s+$", "");
				if (!lineExpected.equals(lineOutput)) {
					result = FAIL;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = IO_ERROR;
		}

		try {
			bufferedoutput.close();
			bufferedexpected.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}


	public static void main(String args[]) throws IOException{
		
		String studentID = Compiler.studentID;
		String uciNetID = Compiler.uciNetID;
		
		int privateTestcaseNum = 1;

		int privatePass = 0;
			try{
				if (testPrivate() == PASS){
					++ privatePass;
				}else{
					System.out.println("failed");
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			
		
		
		System.out.print(studentID);
		System.out.print("\t");
		System.out.print(uciNetID);
		System.out.print("\t");
		System.out.print(" Passed Private Cases: ");
		System.out.print(privatePass);
		System.out.print("/");
		System.out.println(privateTestcaseNum);
		
	}
	
	
	
}