package main.java.ordenacao_trending_full_date.medioCaso;

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

public class CountingSort3 {

    public static void main(String[] args) {
        // Marca o início da execução do programa para medir o tempo de execução
        long startTime = System.currentTimeMillis();

        // Define o caminho do arquivo CSV de entrada e de saída
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_countingSort_medioCaso.csv");

        // Define o índice da coluna a ser ordenada ("trending_full_date")
        int dateIndex = 2;

        // Verifica se o arquivo de entrada existe
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o conteúdo do CSV para um array de strings bidimensional
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            // Remove espaços em branco da coluna de data
            cleanSpacesInColumn(data, dateIndex);

            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            // Exibe os valores da coluna "trending_full_date" antes da ordenação
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Inicia a ordenação usando Counting Sort em ordem decrescente
            System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
            countingSort(data, dateIndex);

            // Escreve os dados ordenados no arquivo CSV de saída
            writeCsv(data, outputPath);
            System.out.println("Arquivo ordenado salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Calcula e exibe o tempo de execução e o uso de memória
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Função que implementa o algoritmo Counting Sort para ordenar a coluna de datas
    public static void countingSort(String[][] data, int columnIndex) {
        // Converte as datas da coluna "trending_full_date" para números no formato yyyymmdd
        int[] numericDates = new int[data.length - 1];
        for (int i = 1; i < data.length; i++) {
            numericDates[i - 1] = convertDateToNumeric(data[i][columnIndex]);
        }

        // Encontra o valor máximo e mínimo no array de datas numéricas
        int min = Arrays.stream(numericDates).min().orElseThrow();
        int max = Arrays.stream(numericDates).max().orElseThrow();

        // Inicializa o array de contagem para armazenar a frequência de cada data
        int[] count = new int[max - min + 1];

        // Preenche o array de contagem com a frequência de cada data
        for (int numericDate : numericDates) {
            count[numericDate - min]++;
        }

        // Modifica o array de contagem para armazenar as posições finais (ordem decrescente)
        for (int i = count.length - 2; i >= 0; i--) {
            count[i] += count[i + 1];
        }

        // Cria um novo array para armazenar os dados ordenados
        String[][] sortedData = new String[data.length][];
        sortedData[0] = data[0]; // Copia os cabeçalhos

        // Preenche o array ordenado com base no array de contagem
        for (int i = 0; i < numericDates.length; i++) {
            int numericDate = numericDates[i];
            int position = count[numericDate - min]--;
            sortedData[position] = data[i + 1]; // Coloca a linha na posição correta
        }

        // Verificação para garantir que o array ordenado não contenha valores nulos
        for (int i = 1; i < sortedData.length; i++) {
            if (sortedData[i] == null) {
                System.err.println("Elemento nulo encontrado em sortedData na posição " + i);
                throw new IllegalStateException("Elemento nulo encontrado em sortedData na posição " + i);
            }
        }

        // Copia o array ordenado de volta para o array original
        for (int i = 1; i < data.length; i++) {
            data[i] = sortedData[i];
        }
    }

    // Função que converte uma data no formato dd/MM/yyyy para um número no formato yyyymmdd
    public static int convertDateToNumeric(String date) {
        String[] parts = date.split("/");
        return Integer.parseInt(parts[2] + parts[1] + parts[0]); // Concatena yyyy + mm + dd
    }

    // Função para ler o conteúdo de um arquivo CSV e retorná-lo em um array de strings bidimensional
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

            // Define o formato do CSV para leitura
            CSVFormat csvFormat = CSVFormat.DEFAULT.withTrim().withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array para cada nova linha
                data[rowCount] = record.stream().toArray(String[]::new); // Converte o registro para um array de strings
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

    // Função que remove espaços em branco de uma coluna específica
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco no início e fim
            }
        }
    }

    // Função que expande o tamanho de um array bidimensional para acomodar mais linhas
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Função que escreve os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                if (row != null) {
                    printer.printRecord(Arrays.asList(row));  // Grava apenas as linhas não nulas
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
