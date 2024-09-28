package main.java.ordenacao_channel_title.medioCaso;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

public class MergeSort {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_mergeSort_medioCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe antes de tentar ler
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Carregar dados do CSV
        String[][] data = readCsv(inputPath);

        // Verifica se os dados foram lidos corretamente
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex); // Remove espaços iniciais na coluna especificada

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data); // Preenche as linhas que têm menos colunas do que o cabeçalho

            // Verifica se já está ordenado
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação...");
                try {
                    sortByChannelTitle(data, columnIndex); // Ordena os dados pela coluna "channel_title"
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

    // Verifica se os dados já estão ordenados na coluna especificada
    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            String[] previousRow = data[i - 1];
            String[] currentRow = data[i];

            if (previousRow.length <= columnIndex || currentRow.length <= columnIndex) {
                continue; // Ignora se não há colunas suficientes
            }

            String previousValue = previousRow[columnIndex].trim();
            String currentValue = currentRow[columnIndex].trim();

            if (previousValue.isEmpty() || currentValue.isEmpty()) {
                continue; // Ignora se os valores estão vazios
            }

            // Verifica se os dados estão em ordem crescente
            if (previousValue.compareToIgnoreCase(currentValue) > 0) {
                return false; // Retorna falso se encontrar uma inversão
            }
        }
        return true; // Retorna verdadeiro se os dados estão ordenados
    }

    // Lê um arquivo CSV e retorna os dados como um array bidimensional
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a linha do cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Separa os cabeçalhos
            data = new String[1][headers.length]; // Cria o array para armazenar os dados
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
                    continue; // Ignora linhas com colunas insuficientes
                }
                data = expandArray(data, rowCount + 1); // Expande o array para adicionar a nova linha
                data[rowCount] = record.stream().toArray(String[]::new); // Adiciona a linha ao array
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
        return data; // Retorna os dados lidos
    }

    // Expande um array bidimensional
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][];
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia os dados do array original
        return newArray; // Retorna o novo array
    }

    // Remove espaços iniciais na coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Removendo espaços iniciais na coluna " + columnIndex + "...");
        for (int i = 1; i < data.length; i++) { // Começa da linha 1 para ignorar o cabeçalho
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = removeLeadingAndTrailingSpaces(row[columnIndex]); // Limpa espaços
                System.out.println("Linha " + i + " após limpeza: " + Arrays.toString(row));
            }
        }
    }

    // Remove espaços em branco do início e do fim de uma string
    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", ""); // Remove espaços
    }

    // Preenche colunas ausentes com strings vazias
    public static void fillMissingColumns(String[][] data) {
        String[] headers = data[0];
        int numColumns = headers.length;

        System.out.println("Preenchendo colunas ausentes...");
        for (int i = 1; i < data.length; i++) { // Começa da linha 1 para ignorar o cabeçalho
            String[] row = data[i];
            if (row.length < numColumns) {
                String[] newRow = new String[numColumns]; // Cria um novo array com o número correto de colunas
                System.arraycopy(row, 0, newRow, 0, row.length); // Copia a linha original
                for (int j = row.length; j < numColumns; j++) {
                    newRow[j] = ""; // Preenche as colunas restantes com strings vazias
                }
                data[i] = newRow; // Atualiza a linha no array
                System.out.println("Linha " + i + " após preenchimento: " + Arrays.toString(newRow));
            }
        }
    }

    // Ordena os dados pela coluna "channel_title"
    public static void sortByChannelTitle(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return; // Retorna se o índice da coluna for inválido
        }

        String[] header = data[0]; // Armazena o cabeçalho
        String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length); // Cria um array apenas com os dados (sem cabeçalho)

        // Ordenar pelo título do canal
        System.out.println("Ordenando dados pela coluna: " + header[columnIndex]);
        Arrays.sort(dataToSort, (row1, row2) -> {
            String value1 = row1[columnIndex].trim();
            String value2 = row2[columnIndex].trim();
            return value1.compareToIgnoreCase(value2); // Compara em ordem alfabética
        });

        // Reconstruir o array completo com cabeçalho
        String[][] sortedData = new String[dataToSort.length + 1][header.length];
        sortedData[0] = header; // Define o cabeçalho
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length); // Copia os dados ordenados

        // Atualiza o array original
        System.arraycopy(sortedData, 0, data, 0, sortedData.length); // Copia de volta para o array original
    }

    // Escreve os dados ordenados em um novo arquivo CSV
    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL))) {
            for (String[] row : data) {
                csvPrinter.printRecord((Object[]) row); // Imprime cada linha no CSV
            }
            System.out.println("Arquivo salvo em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
