import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class bf {
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("No file path provided.");
			System.exit(1);
		}
		String filePath = args[0];
		Path path = Paths.get(filePath);
		if (!Files.exists(path)) {
			System.err.println("File path " + filePath + " does not exist.");
			System.exit(1);
		}
		String program = Files.readString(path);
		byte[] memory = new byte[32768];
		int pointer = 0;
		int pc = 0;
		String input = "";
		Map<Integer, Integer> loops = parseLoops(program);
		while (pc < program.length()) {
			char cmd = program.charAt(pc);
			switch (cmd) {
				case '>':
					if (pointer == 32767) {
						pointer = 0;
					} else {
						pointer++;
					}
					break;
				case '<':
					if (pointer == 0) {
						pointer = 32767;
					} else {
						pointer--;
					}
					break;
				case '+':
					memory[pointer]++;
					break;
				case '-':
					memory[pointer]--;
					break;
				case '.':
					System.out.print((char) memory[pointer]);
					break;
				case ',':
					Scanner sc = new Scanner(System.in);
					while (true) {
						try {
							System.out.print("Input [00-FF]> ");
							input = sc.nextLine().trim().toUpperCase();
							if (input.length() == 1) {
								input = "0" + input;
							} else if (input.length() > 2 || input.length() == 0) {
								throw new NumberFormatException();
							}
							memory[pointer] = (byte) Integer.parseInt(input, 16);
							break;
						} catch (NumberFormatException e) {
							System.err.println("Input " + input + " is invalid.");
						}
					}
					break;
				case '[':
					if (memory[pointer] == 0) {
						pc = loops.get(pc);
					}
					break;
				case ']':
					if (memory[pointer] != 0) {
						pc = loops.get(pc);
					}
					break;
			}
			pc++;
		}
		System.exit(0);
	}

	public static Map<Integer, Integer> parseLoops(String program) {
		Map<Integer, Integer> loops = new HashMap<>();
		Stack<Integer> stack = new Stack<>();
		for (int i = 0; i < program.length(); i++) {
			if (program.charAt(i) == '[') {
				stack.push(i);
			} else if (program.charAt(i) == ']') {
				int open = stack.pop();
				loops.put(open, i);
				loops.put(i, open);
			}
		}
		return loops;
	}
}