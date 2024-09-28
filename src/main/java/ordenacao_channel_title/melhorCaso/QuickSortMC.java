package main.java.ordenacao_channel_title.melhorCaso;

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

public class QuickSortMC {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Definição dos caminhos de entrada e saída dos arquivos CSV
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_quickSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_quickSort_melhorCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title" no CSV

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Carrega os dados do CSV em um array simples
        String[][] data = readCsv(inputPath);

        // Verifica se há dados para processar
        if (data != null && data.length > 1) {
            cleanSpacesInColumn(data, columnIndex); // Remove espaços extras nas strings
            fillMissingColumns(data); // Preenche linhas com menos colunas

            // Verifica se o CSV já está ordenado em ordem crescente
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação...");
                try {
                    quickSort(data, 1, data.length - 1, columnIndex); // Ordena com QuickSort
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                }
            }

            // Grava o CSV ordenado em um novo arquivo
            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Exibe tempo de execução e memória utilizada
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Verifica se os dados estão ordenados em ordem crescente com base em uma coluna
    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            String[] previousRow = data[i - 1];
            String[] currentRow = data[i];

            if (previousRow.length <= columnIndex || currentRow.length <= columnIndex) {
                continue;
            }

            String previousValue = previousRow[columnIndex].trim();
            String currentValue = currentRow[columnIndex].trim();

            if (previousValue.isEmpty() || currentValue.isEmpty()) {
                continue;
            }

            // Verifica se está em ordem crescente
            if (previousValue.compareToIgnoreCase(currentValue) > 0) {
                return false;
            }
        }
        return true;
    }

    // Lê o CSV e carrega os dados em um array simples de strings
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê o cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(",");
            data = new String[1][headers.length];
            data[0] = headers;

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withTrim().withQuoteMode(QuoteMode.ALL));
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array conforme necessário
                data[rowCount] = record.stream().toArray(String[]::new);
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Expande o tamanho de um array 2D
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Remove espaços no início e fim das strings de uma coluna específica
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = removeLeadingAndTrailingSpaces(row[columnIndex]);
            }
        }
    }

    // Remove espaços no início e no fim de uma string
    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    // Preenche as colunas ausentes nas linhas que têm menos colunas do que o cabeçalho
    public static void fillMissingColumns(String[][] data) {
        String[] headers = data[0];
        int numColumns = headers.length;

        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length < numColumns) {
                String[] newRow = new String[numColumns];
                System.arraycopy(row, 0, newRow, 0, row.length);
                for (int j = row.length; j < numColumns; j++) {
                    newRow[j] = "";
                }
                data[i] = newRow;
            }
        }
    }

    // Algoritmo de ordenação QuickSort
    public static void quickSort(String[][] data, int low, int high, int columnIndex) {
        while (low < high) {
            int pivotIndex = partition(data, low, high, columnIndex);

            // Ordena a menor partição primeiro para otimizar a recursão
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(data, low, pivotIndex - 1, columnIndex);
                low = pivotIndex + 1; // Loop continua na partição maior
            } else {
                quickSort(data, pivotIndex + 1, high, columnIndex);
                high = pivotIndex - 1; // Loop continua na partição menor
            }
        }
    }

    // Função de partição para o QuickSort, usando a última linha como pivô
    private static int partition(String[][] data, int low, int high, int columnIndex) {
        String pivot = data[high][columnIndex].trim();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (data[j][columnIndex].trim().compareToIgnoreCase(pivot) < 0) { // Ordenação crescente
                i++;
                swap(data, i, j); // Troca as linhas
            }
        }
        swap(data, i + 1, high); // Coloca o pivô na posição correta
        return i + 1;
    }

    // Troca duas linhas no array
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // Grava os dados ordenados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withHeader(data[0]))) {
            for (int i = 1; i < data.length; i++) {
                printer.printRecord(data[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
