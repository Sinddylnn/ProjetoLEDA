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

public class QuickSortMedianaPC3 {

    public static void main(String[] args) {
        // Marca o início do tempo de execução
        long startTime = System.currentTimeMillis();

        // Define os caminhos de entrada e saída para os arquivos CSV
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSortMediana_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSortMediana_piorCaso.csv");

        // Índice da coluna de data ("trending_full_date")
        int dateIndex = 2;

        // Imprime mensagens de log sobre o processo
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo CSV de entrada existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o arquivo CSV e armazena os dados em um array bidimensional
        String[][] data = readCsv(inputPath);

        // Verifica se há dados suficientes para continuar o processamento
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");

            // Limpa os espaços em branco no início da coluna "trending_full_date"
            cleanSpacesInColumn(data, dateIndex);

            System.out.println("Iniciando inversão da ordem por trending_full_date...");

            // Inverte a ordem dos dados na coluna "trending_full_date"
            reverseOrder(data, 1, data.length - 1, dateIndex);

            // Salva os dados invertidos em um novo arquivo CSV
            writeCsv(data, outputPath);
            System.out.println("Arquivo salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para inverter ou o arquivo está vazio.");
        }

        // Calcula o tempo de execução e o uso de memória
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        // Exibe o tempo de execução e a memória usada
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Função que inverte a ordem dos dados entre os índices low e high na coluna especificada
    public static void reverseOrder(String[][] data, int low, int high, int columnIndex) {
        while (low < high) {
            swap(data, low, high); // Troca os elementos
            low++;
            high--;
        }
    }

    // Função auxiliar que troca as linhas data[i] e data[j]
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i]; // Armazena temporariamente a linha
        data[i] = data[j];       // Substitui a linha i pela linha j
        data[j] = temp;          // Coloca a linha temporária no lugar da linha j
    }

    // Função que lê o arquivo CSV e retorna os dados em um array bidimensional
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a linha do cabeçalho

            // Verifica se o cabeçalho é nulo
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            // Divide o cabeçalho em colunas
            String[] headers = headerLine.split(",");
            data = new String[1][headers.length]; // Cria o array de dados
            data[0] = headers; // Armazena os cabeçalhos

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            // Configuração do formato CSV
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim() // Remove espaços extras
                    .withQuoteMode(QuoteMode.ALL); // Coloca aspas em todos os campos

            // Usa o CSVParser para processar o arquivo CSV
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;

            // Itera sobre cada registro no arquivo CSV
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    // Ignora a linha se tiver menos colunas que o cabeçalho
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                // Expande o array de dados conforme necessário
                data = expandArray(data, rowCount + 1);
                data[rowCount] = record.stream().toArray(String[]::new); // Armazena os dados
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

    // Função que remove espaços em branco no início dos valores da coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove os espaços no início
            }
        }
    }

    // Função que expande o tamanho do array de dados para acomodar mais linhas
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][]; // Cria um novo array com o novo tamanho
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia os dados para o novo array
        return newArray;
    }

    // Função que escreve os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT
                .withHeader(data[0]) // Escreve o cabeçalho
                .withQuoteMode(QuoteMode.ALL))) { // Coloca aspas em todos os campos
            for (int i = 1; i < data.length; i++) {
                printer.printRecord(data[i]); // Escreve cada linha no arquivo CSV
            }
            printer.flush(); // Garante que todos os dados sejam escritos
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}
