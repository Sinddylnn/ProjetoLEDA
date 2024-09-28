package main.java.ordenacao_trending_full_date.piorCaso;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SelectionSortPC3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos para o arquivo de entrada e saída
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_selectionSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_selectionSort_piorCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o arquivo CSV
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex);

            // Verifica se a coluna está ordenada
            if (isSorted(data, dateIndex)) {
                System.out.println("A coluna já está ordenada de maneira crescente.");
                return; // Sai do programa se já estiver ordenado
            }

            System.out.println("Valores da coluna trending_full_date antes da inversão:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Inverte a ordem da coluna
            invertColumn(data, dateIndex);

            System.out.println("Valores da coluna trending_full_date após a inversão:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Escreve os dados no arquivo de saída
            writeCsv(data, outputPath);
            System.out.println("Arquivo reordenado salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para verificar se a coluna de datas está ordenada
    public static boolean isSorted(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            if (compareDates(data[i][columnIndex], data[i + 1][columnIndex]) > 0) {
                return false; // Se encontrar uma data fora de ordem, retorna falso
            }
        }
        return true; // Se nenhuma data estiver fora de ordem, retorna verdadeiro
    }

    // Método para inverter a ordem dos elementos na coluna especificada
    public static void invertColumn(String[][] data, int columnIndex) {
        int n = data.length;
        for (int i = 1; i < n / 2; i++) {
            swap(data, i, n - 1 - i); // Troca elementos simetricamente
        }
    }

    // Método para trocar elementos no array
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // Método para comparar as datas
    public static int compareDates(String date1, String date2) {
        // Formato da data: dd/MM/yyyy
        String[] parts1 = date1.split("/");
        String[] parts2 = date2.split("/");

        // Compara os anos
        int yearComparison = Integer.compare(Integer.parseInt(parts1[2]), Integer.parseInt(parts2[2]));
        if (yearComparison != 0) return yearComparison;

        // Compara os meses
        int monthComparison = Integer.compare(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]));
        if (monthComparison != 0) return monthComparison;

        // Compara os dias
        return Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
    }

    // Método para ler o arquivo CSV
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(",");
            data = new String[1][headers.length];
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1);
                data[rowCount] = record.stream().toArray(String[]::new);
                rowCount++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data;
    }

    // Método para limpar espaços em branco na coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Método para expandir um array
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Método para escrever dados no arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord(row);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
