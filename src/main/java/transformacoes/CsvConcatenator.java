package main.java.transformacoes;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CsvConcatenator {

    public static void main(String[] args) {
        String directoryPath = "caminho/para/seus/arquivoscsv";
        String outputFilePath = "caminho/para/videos.csv";

        try {
            List<File> csvFiles = listCsvFiles(directoryPath);
            concatenateCsvFiles(csvFiles, outputFilePath);
            System.out.println("Arquivos concatenados com sucesso!");
        } catch (IOException e) {
            System.out.println("Ocorreu um erro: " + e.getMessage());
        }
    }

    // Função que lista todos os arquivos .csv no diretório
    private static List<File> listCsvFiles(String directoryPath) {
        File folder = new File(directoryPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        return files != null ? Arrays.asList(files) : new ArrayList<>();
    }

    // Função que concatena os arquivos CSV
    private static void concatenateCsvFiles(List<File> csvFiles, String outputFilePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
        boolean isFirstFile = true;

        for (File csvFile : csvFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;

            // Escreve o cabeçalho apenas do primeiro arquivo
            if (isFirstFile && (line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                isFirstFile = false;
            } else {
                // Ignora o cabeçalho dos outros arquivos
                reader.readLine();
            }

            // Escreve o conteúdo dos arquivos
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader.close();
        }

        writer.close();
    }
}
