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

public class CountingSortPC3 {

    public static void main(String[] args) {
        // Marca o início da execução do programa para medir o tempo de execução
        long startTime = System.currentTimeMillis();

        // Caminho do arquivo CSV de entrada e saída
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_countingSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_countingSort_piorCaso.csv");

        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo de entrada existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o arquivo CSV e armazena os dados em uma matriz de strings
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex); // Limpa os espaços em branco na coluna "trending_full_date"

            // Exibe os valores da coluna "trending_full_date" antes da inversão
            System.out.println("Valores da coluna trending_full_date antes da inversão:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Inverte a ordem dos dados
            reverseArray(data);

            // Exibe os valores da coluna "trending_full_date" após a inversão
            System.out.println("Valores da coluna trending_full_date após a inversão:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Escreve os dados invertidos em um novo arquivo CSV
            writeCsv(data, outputPath);
            System.out.println("Arquivo invertido salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Exibe o tempo de execução e a memória utilizada
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para inverter a ordem dos dados
    // Inverte o conteúdo da matriz de strings, exceto a primeira linha (cabeçalhos)
    public static void reverseArray(String[][] data) {
        for (int i = 1, j = data.length - 1; i < j; i++, j--) {
            String[] temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }
    }

    // Método para ler o conteúdo de um arquivo CSV e armazená-lo em uma matriz de strings
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            // Lê os cabeçalhos e os armazena na primeira linha da matriz
            String[] headers = headerLine.split(",");
            data = new String[1][headers.length];
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            // Define o formato CSV e cria um parser
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;

            // Itera pelas linhas do arquivo CSV e as armazena na matriz
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande a matriz para adicionar mais linhas
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

    // Método para remover espaços em branco de uma coluna específica
    // Limpa os espaços em branco ao redor dos valores da coluna especificada (dateIndex)
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Método para expandir a matriz de dados
    // Aumenta o tamanho da matriz para armazenar mais linhas de dados
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia os dados da matriz original para a nova
        return newArray;
    }

    // Método para escrever os dados em um arquivo CSV
    // Grava os dados da matriz no arquivo CSV especificado
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                if (row != null) {
                    printer.printRecord(Arrays.asList(row));  // Assegura que apenas linhas não nulas sejam gravadas
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
