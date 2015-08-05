import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MankolistonVastiganto {
	
	private static final Logger logger = Logger.getLogger(MankolistonVastiganto.class.getName());
	
	private final File dosierujo, modelo;
	
	private final List<String> radikoj;
	
	private String modelenhavo;

	public static void main(String args[]) throws IOException {
		final MankolistonVastiganto vml;
		final File dosierujo, modelo;
		
		if(args.length != 2) {
			logger.warning("Oni postulas du argumentojn: dosierujo modelo");
			System.exit(-1);
		}
		dosierujo = new File(args[0]);
		if(!dosierujo.exists() || !dosierujo.isDirectory()) {
			logger.warning("Dosierujo nepre ekzistu kaj estu dosierujo.");
			System.exit(-2);
		}
		modelo = new File(args[1]);
		if(!modelo.exists() || modelo.isDirectory()) {
			logger.warning("Modelo nepre ekzistu kaj estu dosiero");
			System.exit(-3);
		}
		vml = new MankolistonVastiganto(dosierujo, modelo);
		vml.vastigu();
	}
	
	public MankolistonVastiganto(File dosierujo, File modelo) {
		this.dosierujo = dosierujo;
		this.modelo = modelo;
		radikoj = new ArrayList<>();
	}
	
	public void vastigu() throws IOException {
		sarguListon();
		if(radikoj.size() == 0) {
			logger.warning("Malplena dosiero");
			return;
		}
		sarguModelon();
		radikoj.forEach(this::kreuDosieron);
	};
	
	private void sarguListon() throws IOException {
		final File mankolisto;
		String read;
		
		mankolisto = new File(dosierujo, "mankolisto.txt");
		if(!mankolisto.exists())
			return;
		try(BufferedReader reader = openReader(mankolisto)) {
			while((read = reader.readLine()) != null)
				radikoj.add(read);
		}
	}
	
	private void sarguModelon() throws IOException {
		final StringBuilder builder;
		String read;
		
		builder = new StringBuilder();
		try(BufferedReader reader = openReader(modelo)) {
			while((read = reader.readLine()) != null)
				builder.append(read).append('\n');
		}
		modelenhavo = builder.toString();		
	}
	
	private void kreuDosieron(String radiko) {
		final File eliro;
		
		try {
			eliro = new File(dosierujo, String.format("%s.json", radiko));
			if(eliro.exists())
				return;
			try(OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(eliro), "UTF-8");
					PrintWriter printer = new PrintWriter(new BufferedWriter(osw))) {
				printer.write(modelenhavo);
			}
		}
		catch(IOException ioex) {
			logger.log(Level.SEVERE, "Eraro kauzita de la kreado de la dosiero por la radiko {0}:{1}",
					new Object[]{radiko, ioex.getMessage()});
		}
	}
	
	private BufferedReader openReader(File file) throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	}
}
