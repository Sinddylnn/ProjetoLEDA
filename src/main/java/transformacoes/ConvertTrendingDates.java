package main.java.transformacoes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConvertTrendingDates {

    public static void convertTrendingDates(String inputFile, String outputFile) {
        try (
                FileReader reader = new FileReader(inputFile);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
                FileWriter writer = new FileWriter(outputFile);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withQuoteMode(null))
        ) {
            // Verifica se a coluna 'trending_date' existe
            if (!csvParser.getHeaderMap().containsKey("trending_date")) {
                System.out.println("A coluna 'trending_date' não foi encontrada no arquivo.");
                return;
            }

            // Itera sobre as linhas do CSV
            boolean headerPrinted = false;
            for (CSVRecord record : csvParser) {
                if (!headerPrinted) {
                    // Imprime o cabeçalho reorganizado com 'trending_full_date' na posição 3
                    csvPrinter.printRecord("video_id", "title", "trending_full_date", "channel_title", "trending_date");
                    headerPrinted = true;
                }

                String trendingDate = record.get("trending_date");
                String fullDate = convertDate(trendingDate);

                // Imprime os valores das colunas, incluindo a nova coluna 'trending_full_date'
                csvPrinter.printRecord(record.get("video_id"), record.get("title"), fullDate, record.get("channel_title"), trendingDate);
            }

            System.out.println("Arquivo '" + outputFile + "' gerado com sucesso.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Função para converter a data
    private static String convertDate(String dateStr) {
        String[] parts = dateStr.split("\\.");
        if (parts.length == 3) {
            String year = "20" + parts[0];  // Considerando que o ano está no formato AA
            String day = parts[1];
            String month = parts[2];
            return day + "/" + month + "/" + year;
        }
        return "";
    }

    public static void main(String[] args) {
        String inputFile = "Diretório de entrada/videos.csv";  // Caminho do arquivo de entrada
        String outputFile = "Diretório para saida/videos_T1.csv";  // Caminho do arquivo de saída

        // Chama a função para converter as datas
        convertTrendingDates(inputFile, outputFile);
    }
}
