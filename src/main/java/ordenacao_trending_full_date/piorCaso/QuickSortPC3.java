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

public class QuickSortPC3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSort_piorCaso.csv");

        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex);

            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Check if the column is already sorted
            if (!isSorted(data, dateIndex)) {
                System.out.println("Iniciando ordenação por trending_full_date em ordem crescente...");
                quickSort(data, 1, data.length - 1, dateIndex);
            } else {
                System.out.println("A coluna já está ordenada. Pulando a etapa de ordenação.");
            }

            writeCsv(data, outputPath);
            System.out.println("Arquivo ordenado salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para verificar se a coluna está ordenada
    private static boolean isSorted(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            if (compareDates(data[i - 1][columnIndex], data[i][columnIndex]) > 0) {
                return false; // Se encontrar um elemento fora de ordem, retorna false
            }
        }
        return true; // Se todos os elementos estiverem em ordem, retorna true
    }

    // Implementação do QuickSort
    public static void quickSort(String[][] data, int left, int right, int columnIndex) {
        if (left < right) {
            int pivotIndex = partition(data, left, right, columnIndex);
            quickSort(data, left, pivotIndex - 1, columnIndex);
            quickSort(data, pivotIndex + 1, right, columnIndex);
        }
    }

    // Função de partição para QuickSort
    private static int partition(String[][] data, int left, int right, int columnIndex) {
        String pivot = data[right][columnIndex];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            // Compara as datas para manter a ordem crescente
            if (compareDates(data[j][columnIndex], pivot) <= 0) {
                i++;
                swap(data, i, j);
            }
        }
        swap(data, i + 1, right);
        return i + 1;
    }

    // Método para trocar duas linhas
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // Método para comparar datas
    private static int compareDates(String date1, String date2) {
        String[] date1Parts = date1.split("/");
        String[] date2Parts = date2.split("/");

        int year1 = Integer.parseInt(date1Parts[2]);
        int month1 = Integer.parseInt(date1Parts[1]);
        int day1 = Integer.parseInt(date1Parts[0]);

        int year2 = Integer.parseInt(date2Parts[2]);
        int month2 = Integer.parseInt(date2Parts[1]);
        int day2 = Integer.parseInt(date2Parts[0]);

        // Comparar primeiro por ano, depois por mês, depois por dia
        if (year1 != year2) {
            return Integer.compare(year1, year2); // Ascendente para ordem crescente
        } else if (month1 != month2) {
            return Integer.compare(month1, month2); // Ascendente para ordem crescente
        } else {
            return Integer.compare(day1, day2); // Ascendente para ordem crescente
        }
    }

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

    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

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
