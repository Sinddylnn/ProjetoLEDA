package main.java.transformacoes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilterDislikes {

    public static void filterDislikesGreaterThanLikes(String inputCsv, String outputCsv) throws IOException {
        // Verifica se o diretório de saída existe, se não, cria
        Path outputPath = Paths.get(outputCsv);
        if (!Files.exists(outputPath.getParent())) {
            Files.createDirectories(outputPath.getParent());
        }

        BufferedReader reader = new BufferedReader(new FileReader(inputCsv));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsv));

        String headerLine = reader.readLine();
        if (headerLine == null) {
            throw new IOException("O arquivo CSV está vazio.");
        }

        String[] fieldnames = headerLine.split(",");

        // Verificar se as colunas 'likes' e 'dislikes' estão presentes
        boolean hasLikes = false, hasDislikes = false;
        for (String fieldname : fieldnames) {
            if (fieldname.trim().equals("likes")) {
                hasLikes = true;
            }
            if (fieldname.trim().equals("dislikes")) {
                hasDislikes = true;
            }
        }
        if (!hasLikes || !hasDislikes) {
            throw new IOException("O arquivo CSV não contém as colunas 'likes' e 'dislikes'.");
        }

        writer.write(headerLine);
        writer.newLine();

        String line;
        int filteredLines = 0;

        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            try {
                int likes = Integer.parseInt(values[getIndex(fieldnames, "likes")].replace(",", "").trim());
                int dislikes = Integer.parseInt(values[getIndex(fieldnames, "dislikes")].replace(",", "").trim());

                if (dislikes > likes) {
                    writer.write(line);
                    writer.newLine();
                    filteredLines++;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                continue; // Pular linhas onde likes ou dislikes não são números válidos
            }
        }

        reader.close();
        writer.close();

        System.out.println("Total de linhas filtradas e escritas: " + filteredLines);
    }

    private static int getIndex(String[] headers, String target) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equals(target)) {
                return i;
            }
        }
        return -1; // Retorna -1 se não encontrar
    }

    public static void main(String[] args) {
        String inputCsvFile = "diretório do videos_T1.csv";
        String outputCsvFile = "diretório do videos_T2.csv";

        try {
            System.out.println("Filtrando vídeos onde dislikes são maiores que likes...");
            filterDislikesGreaterThanLikes(inputCsvFile, outputCsvFile);
            System.out.println("Processo concluído.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
