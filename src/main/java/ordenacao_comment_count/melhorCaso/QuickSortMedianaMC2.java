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

public class QuickSortMedianaMC2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_quickSortMediana_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_quickSortMediana_melhorCaso.csv");
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
            if (isAlreadySortedCrescente(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados em ordem crescente.");
            } else {
                System.out.println("Iniciando ordenação crescente...");
                try {
                    quickSortCrescente(data, 1, data.length - 1, columnIndex);
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

    // Função para verificar se está em ordem crescente
    public static boolean isAlreadySortedCrescente(String[][] data, int columnIndex) {
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

            try {
                double previousNum = Double.parseDouble(previousValue);
                double currentNum = Double.parseDouble(currentValue);

                if (previousNum > currentNum) {
                    return false;
                }
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter valor para número: " + previousValue + ", " + currentValue);
                continue;
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

    // Expande um array bidimensional
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

    // QuickSort em ordem crescente
    public static void quickSortCrescente(String[][] data, int low, int high, int columnIndex) {
        if (low < high) {
            int pivotIndex = partitionCrescente(data, low, high, columnIndex);
            quickSortCrescente(data, low, pivotIndex - 1, columnIndex);
            quickSortCrescente(data, pivotIndex + 1, high, columnIndex);
        }
    }

    private static int partitionCrescente(String[][] data, int low, int high, int columnIndex) {
        int pivotIndex = medianOfThree(data, low, high, columnIndex);
        String pivot = data[pivotIndex][columnIndex].trim();
        swap(data, pivotIndex, high); // Mover o pivô para o final
        int i = low - 1;

        for (int j = low; j < high; j++) {
            try {
                double currentValue = Double.parseDouble(data[j][columnIndex].trim());
                double pivotValue = Double.parseDouble(pivot);

                if (currentValue <= pivotValue)
                {
                    i++;
                    swap(data, i, j);
                }
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter valor para número: " + e.getMessage());
            }
        }
        swap(data, i + 1, high); // Coloca o pivô em sua posição correta
        return i + 1;
    }

    // Função para encontrar a mediana de três e otimizar o QuickSort
    private static int medianOfThree(String[][] data, int low, int high, int columnIndex) {
        int mid = (low + high) / 2;

        try {
            double lowValue = Double.parseDouble(data[low][columnIndex].trim());
            double midValue = Double.parseDouble(data[mid][columnIndex].trim());
            double highValue = Double.parseDouble(data[high][columnIndex].trim());

            if ((lowValue > midValue) != (lowValue > highValue)) {
                return low;
            } else if ((midValue > lowValue) != (midValue > highValue)) {
                return mid;
            } else {
                return high;
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter valor para número: " + e.getMessage());
            return mid; // Retorna o valor do meio em caso de erro
        }
    }

    // Função para trocar duas linhas no array
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // Função para escrever os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path outputPath) {
        try (FileWriter out = new FileWriter(outputPath.toFile());
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {

            for (String[] row : data) {
                printer.printRecord((Object[]) row);
            }
            System.out.println("Arquivo CSV salvo em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}
