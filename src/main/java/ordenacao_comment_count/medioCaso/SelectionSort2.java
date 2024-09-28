package main.java.ordenacao_comment_count.medioCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SelectionSort2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_selectionSort_medioCaso.csv");
        int columnIndex = 11; // Índice da coluna "comment_count"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            System.out.println("Valores da coluna comment_count antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][columnIndex]);
            }

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            // Verifica se já está ordenado em ordem crescente
            if (isAlreadySorted(data, columnIndex, true)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação por Selection Sort...");
                try {
                    sortByCommentCount(data, columnIndex);
                    System.out.println("Ordenação por Selection Sort finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

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

    // Função para ordenar a coluna 11 (comment_count) em ordem crescente
    public static void sortByCommentCount(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return;
        }

        String[] header = data[0];
        String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length);

        // Ordenar pelo comment_count como números em ordem crescente
        System.out.println("Iniciando ordenação pelo comment_count...");
        for (int i = 0; i < dataToSort.length - 1; i++) {
            for (int j = i + 1; j < dataToSort.length; j++) {
                int valueI = parseCommentCount(dataToSort[i][columnIndex]);
                int valueJ = parseCommentCount(dataToSort[j][columnIndex]);

                if (valueI > valueJ) { // Troca se i for maior que j
                    String[] temp = dataToSort[i];
                    dataToSort[i] = dataToSort[j];
                    dataToSort[j] = temp;
                }
            }
        }

        // Concatenar o cabeçalho de volta aos dados ordenados
        String[][] sortedData = new String[data.length][];
        sortedData[0] = header;
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length);
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);
        System.out.println("Ordenação concluída.");
    }

    // Método para tentar converter valores da coluna comment_count para inteiros
    private static int parseCommentCount(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.out.println("Valor não numérico encontrado: " + value + ". Atribuindo valor -1.");
            return -1; // Valor padrão para entradas não numéricas
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

    public static void fillMissingColumns(String[][] data) {
        int maxColumns = Arrays.stream(data).mapToInt(row -> row.length).max().orElse(0);
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < maxColumns) {
                data[i] = Arrays.copyOf(data[i], maxColumns); // Preenche colunas ausentes
            }
        }
    }

    public static boolean isAlreadySorted(String[][] data, int columnIndex, boolean ascending) {
        for (int i = 1; i < data.length - 1; i++) {
            int currentValue = parseCommentCount(data[i][columnIndex]);
            int nextValue = parseCommentCount(data[i + 1][columnIndex]);

            if (ascending) {
                if (currentValue > nextValue) {
                    return false; // Não está ordenado em ordem crescente
                }
            } else {
                if (currentValue < nextValue) {
                    return false; // Não está ordenado em ordem decrescente
                }
            }
        }
        return true; // Dados estão ordenados
    }

    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL))) {
            for (String[] row : data) {
                printer.printRecord(row);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }
}
