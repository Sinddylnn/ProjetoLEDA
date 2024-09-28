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

public class MergeSort3 {  // Classe principal para a ordenação de dados usando Merge Sort

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();  // Captura o tempo inicial de execução

        // Define os caminhos de entrada e saída para os arquivos CSV
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_mergeSort_medioCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath);  // Lê os dados do arquivo CSV

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex);  // Limpa espaços em branco na coluna especificada

            // Verifica se o arquivo já está ordenado em ordem decrescente
            if (isSortedDescending(data, dateIndex)) {
                System.out.println("O arquivo já está ordenado em ordem decrescente. Nenhuma ordenação necessária.");
            } else {
                // Imprime os valores da coluna antes da ordenação
                System.out.println("Valores da coluna trending_full_date antes da ordenação:");
                for (int i = 1; i < data.length; i++) {
                    System.out.println(data[i][dateIndex]);
                }

                System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
                mergeSort(data, dateIndex, 1, data.length - 1);  // Ordena os dados usando Merge Sort

                // Imprime os valores da coluna após a ordenação
                System.out.println("Valores da coluna trending_full_date após a ordenação:");
                for (int i = 1; i < data.length; i++) {
                    System.out.println(data[i][dateIndex]);
                }
            }

            writeCsv(data, outputPath);  // Escreve os dados ordenados em um novo arquivo CSV
            System.out.println("Arquivo ordenado salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();  // Captura o tempo final de execução
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();  // Calcula a memória utilizada

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para verificar se os dados estão em ordem decrescente
    private static boolean isSortedDescending(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            if (compareDates(data[i][columnIndex], data[i + 1][columnIndex]) < 0) {
                return false; // Se encontrar uma data que não está em ordem decrescente
            }
        }
        return true; // Se todas as datas estão em ordem decrescente
    }

    // Método para ordenar usando Merge Sort
    public static void mergeSort(String[][] data, int columnIndex, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;  // Calcula o ponto médio

            // Divide o array em duas metades
            mergeSort(data, columnIndex, left, middle);
            mergeSort(data, columnIndex, middle + 1, right);

            // Mescla as duas metades ordenadas
            merge(data, columnIndex, left, middle, right);
        }
    }

    // Método para mesclar as duas metades
    private static void merge(String[][] data, int columnIndex, int left, int middle, int right) {
        int n1 = middle - left + 1;  // Tamanho do sub-array da esquerda
        int n2 = right - middle;      // Tamanho do sub-array da direita

        String[][] leftArray = new String[n1][];  // Cria o sub-array da esquerda
        String[][] rightArray = new String[n2][]; // Cria o sub-array da direita

        // Copia os dados para os arrays temporários
        for (int i = 0; i < n1; i++) {
            leftArray[i] = data[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArray[j] = data[middle + 1 + j];
        }

        // Mescla os arrays temporários
        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            // Compara os elementos para manter a ordem decrescente
            if (compareDates(leftArray[i][columnIndex], rightArray[j][columnIndex]) >= 0) {
                data[k++] = leftArray[i++];
            } else {
                data[k++] = rightArray[j++];
            }
        }

        // Copia os elementos restantes, se houver
        while (i < n1) {
            data[k++] = leftArray[i++];
        }
        while (j < n2) {
            data[k++] = rightArray[j++];
        }
    }

    // Método para comparar datas
    private static int compareDates(String date1, String date2) {
        // Formatação: dd/MM/yyyy
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
            return Integer.compare(year2, year1); // Decrescente
        } else if (month1 != month2) {
            return Integer.compare(month2, month1); // Decrescente
        } else {
            return Integer.compare(day2, day1); // Decrescente
        }
    }

    // Método para ler dados de um arquivo CSV
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];  // Inicializa um array vazio
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a linha do cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Divide o cabeçalho em colunas
            data = new String[1][headers.length];  // Cria o array para os dados
            data[0] = headers;  // Armazena os cabeçalhos

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()  // Remove espaços em branco
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1; // Contador de linhas
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array para incluir a nova linha
                data[rowCount++] = record.stream().toArray(String[]::new); // Armazena os dados da linha
            }

            System.out.println("Linhas lidas: " + (rowCount - 1));
            csvParser.close(); // Fecha o parser
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data; // Retorna os dados lidos
    }

    // Método para expandir o array
    private static String[][] expandArray(String[][] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    // Método para escrever dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT
                .withHeader(data[0]).withQuoteMode(QuoteMode.ALL))) {
            for (int i = 1; i < data.length; i++) {
                printer.printRecord((Object[]) data[i]); // Escreve cada linha no CSV
            }
            printer.flush(); // Garante que todos os dados sejam escritos
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para limpar espaços iniciais em uma coluna específica
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            data[i][columnIndex] = data[i][columnIndex].trim(); // Remove espaços em branco
        }
    }
}
