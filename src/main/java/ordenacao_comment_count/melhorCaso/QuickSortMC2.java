package main.java.ordenacao_comment_count.melhorCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class QuickSortMC2 {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_quickSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_quickSort_melhorCaso.csv");
        int columnIndex = 11; // Índice da coluna "comment_count"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe antes de tentar ler
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Carregar dados do CSV
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            // Verifica se já está ordenado
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação...");
                try {
                    quickSort(data, 1, data.length - 1, columnIndex);
                    System.out.println("Ordenação finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Escreve os dados no arquivo CSV de saída
            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Função para verificar se os dados já estão ordenados em ordem crescente
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

            // Verifica ordem crescente
            if (previousValue.compareToIgnoreCase(currentValue) > 0) {
                return false;
            }
        }
        return true;
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

            CSVFormat csvFormat = CSVFormat.DEFAULT.withTrim().withQuoteMode(QuoteMode.ALL);
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1);
                data[rowCount] = record.stream().toArray(String[]::new);
                System.out.println("Linha " + rowCount + " lida: " + Arrays.toString(data[rowCount]));
                rowCount++;
            }
            System.out.println("Total de linhas lidas: " + rowCount);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data;
    }

    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Removendo espaços iniciais na coluna " + columnIndex + "...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = removeLeadingAndTrailingSpaces(row[columnIndex]);
                System.out.println("Linha " + i + " após limpeza: " + Arrays.toString(row));
            }
        }
    }

    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    public static void fillMissingColumns(String[][] data) {
        String[] headers = data[0];
        int numColumns = headers.length;

        System.out.println("Preenchendo colunas ausentes...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length < numColumns) {
                String[] newRow = new String[numColumns];
                System.arraycopy(row, 0, newRow, 0, row.length);
                for (int j = row.length; j < numColumns; j++) {
                    newRow[j] = "";
                }
                data[i] = newRow;
                System.out.println("Linha " + i + " após preenchimento: " + Arrays.toString(newRow));
            }
        }
    }

    // Função de QuickSort em ordem crescente
    public static void quickSort(String[][] data, int low, int high, int columnIndex) {
        while (low < high) {
            int pivotIndex = partition(data, low, high, columnIndex);

            // Ordenação recursiva na menor partição primeiro para reduzir a profundidade de recursão
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(data, low, pivotIndex - 1, columnIndex);
                low = pivotIndex + 1;  // Repetir loop para a maior partição
            } else {
                quickSort(data, pivotIndex + 1, high, columnIndex);
                high = pivotIndex - 1;  // Repetir loop para a menor partição
            }
        }
    }

    // Função para particionar os dados para QuickSort em ordem crescente
    private static int partition(String[][] data, int low, int high, int columnIndex) {
        String pivot = data[high][columnIndex].trim();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Ordenação em ordem crescente
            if (data[j][columnIndex].trim().compareToIgnoreCase(pivot) < 0) {
                i++;
                swap(data, i, j);
            }
        }
        swap(data, i + 1, high);
        System.out.println("Pivot: " + pivot + ", índice: " + (i + 1));
        return i + 1;
    }

    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord(Arrays.asList(row));
            }
            System.out.println("Arquivo salvo em: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}