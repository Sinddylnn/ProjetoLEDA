package main.java.ordenacao_channel_title.medioCaso;

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

public class QuickSortMediana {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Define os caminhos para o arquivo de entrada e o arquivo de saída.
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_quickSortMediana_medioCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title".

        // Exibe informações sobre o arquivo sendo lido.
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe antes de tentar lê-lo.
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê os dados do CSV e armazena em um array bidimensional.
        String[][] data = readCsv(inputPath);

        // Se houver dados válidos, inicia o processo de limpeza e ordenação.
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            // Verifica se os dados já estão ordenados. Se estiverem, não faz a ordenação.
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados em ordem crescente. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação crescente...");
                try {
                    quickSortCrescente(data, 1, data.length - 1, columnIndex);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Escreve os dados ordenados no arquivo CSV de saída.
            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Exibe o tempo de execução e a memória utilizada.
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Função que verifica se os dados já estão ordenados em ordem crescente na coluna especificada.
    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            String[] previousRow = data[i - 1];
            String[] currentRow = data[i];

            // Ignora linhas que não tenham colunas suficientes.
            if (previousRow.length <= columnIndex || currentRow.length <= columnIndex) {
                continue;
            }

            String previousValue = previousRow[columnIndex].trim();
            String currentValue = currentRow[columnIndex].trim();

            // Ignora valores vazios.
            if (previousValue.isEmpty() || currentValue.isEmpty()) {
                continue;
            }

            // Verifica se os dados não estão em ordem crescente.
            if (previousValue.compareToIgnoreCase(currentValue) > 0) {
                return false;
            }
        }
        return true;
    }

    // Função que lê um arquivo CSV e o converte em uma matriz bidimensional de Strings.
    // A primeira linha é tratada como cabeçalho e as linhas subsequentes como dados.
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

            // Lê cada linha do arquivo e armazena no array.
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

    // Função para expandir o tamanho de uma matriz bidimensional.
    // Utilizada ao adicionar novas linhas no array durante a leitura do CSV.
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Remove espaços em branco no início e no final dos valores da coluna especificada.
    // Isso é feito para garantir que a ordenação seja feita corretamente, sem ser afetada por espaços.
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

    // Função auxiliar que remove espaços em branco no início e no final de uma string.
    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    // Função que preenche linhas com menos colunas do que o cabeçalho com valores vazios.
    // Isso garante que todas as linhas tenham o mesmo número de colunas para evitar erros durante a ordenação.
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

    // Função que implementa o algoritmo QuickSort para ordenar os dados em ordem crescente.
    public static void quickSortCrescente(String[][] data, int low, int high, int columnIndex) {
        if (low < high) {
            int pivotIndex = partitionCrescente(data, low, high, columnIndex);
            quickSortCrescente(data, low, pivotIndex - 1, columnIndex);
            quickSortCrescente(data, pivotIndex + 1, high, columnIndex);
        }
    }

    // Função auxiliar do QuickSort para dividir os dados com base no pivô e organizar os valores ao redor dele.
    private static int partitionCrescente(String[][] data, int low, int high, int columnIndex) {
        String pivot = data[high][columnIndex].trim();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Compara em ordem crescente.
            if (data[j][columnIndex].trim().compareToIgnoreCase(pivot) < 0) {
                i++;
                String[] temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }

        String[] temp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = temp;

        return i + 1;
    }

    // Função que escreve os dados ordenados de volta em um arquivo CSV.
    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord((Object[]) row);
            }
            System.out.println("Arquivo CSV escrito com sucesso em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
