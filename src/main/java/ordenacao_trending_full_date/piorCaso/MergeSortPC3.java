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

public class MergeSortPC3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Definindo os caminhos de entrada e saída do arquivo CSV
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_mergeSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_mergeSort_piorCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        // Exibindo informações sobre a leitura do arquivo
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verificando se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lendo os dados do arquivo CSV
        String[][] data = readCsv(inputPath);

        // Verificando se os dados foram lidos corretamente
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex); // Limpando espaços na coluna especificada

            // Exibindo valores antes da ordenação
            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Verificando se a coluna já está ordenada
            if (!isSorted(data, dateIndex)) {
                System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
                mergeSort(data, dateIndex, 1, data.length - 1); // Ordenando a coluna
            } else {
                System.out.println("A coluna já está ordenada. Pulando a etapa de ordenação.");
            }

            // Invertendo a ordem dos dados após a ordenação
            reverseArray(data);

            // Gravando os dados ordenados em um novo arquivo CSV
            writeCsv(data, outputPath);
            System.out.println("Arquivo ordenado e invertido salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        // Exibindo tempo de execução e memória utilizada
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para inverter a ordem das linhas do array
    private static void reverseArray(String[][] data) {
        int left = 1; // Começa na linha 1 para ignorar o cabeçalho
        int right = data.length - 1;
        while (left < right) {
            String[] temp = data[left];
            data[left] = data[right];
            data[right] = temp;
            left++;
            right--;
        }
        System.out.println("Dados invertidos com sucesso.");
    }

    // Método para verificar se a coluna está ordenada
    private static boolean isSorted(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            if (compareDates(data[i - 1][columnIndex], data[i][columnIndex]) < 0) {
                return false; // Retorna false se encontrar um elemento fora de ordem
            }
        }
        return true; // Retorna true se todos os elementos estiverem em ordem
    }

    // Método para ordenar usando Merge Sort
    public static void mergeSort(String[][] data, int columnIndex, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;

            // Divida o array em duas metades
            mergeSort(data, columnIndex, left, middle);
            mergeSort(data, columnIndex, middle + 1, right);

            // Mescle as duas metades ordenadas
            merge(data, columnIndex, left, middle, right);
        }
    }

    // Método para mesclar as duas metades do array
    private static void merge(String[][] data, int columnIndex, int left, int middle, int right) {
        int n1 = middle - left + 1; // Tamanho da primeira metade
        int n2 = right - middle; // Tamanho da segunda metade

        String[][] leftArray = new String[n1][]; // Array temporário para a primeira metade
        String[][] rightArray = new String[n2][]; // Array temporário para a segunda metade

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
        String[] date1Parts = date1.split("/"); // Divide a data em partes
        String[] date2Parts = date2.split("/");

        int year1 = Integer.parseInt(date1Parts[2]);
        int month1 = Integer.parseInt(date1Parts[1]);
        int day1 = Integer.parseInt(date1Parts[0]);

        int year2 = Integer.parseInt(date2Parts[2]);
        int month2 = Integer.parseInt(date2Parts[1]);
        int day2 = Integer.parseInt(date2Parts[0]);

        // Comparar primeiro por ano, depois por mês, depois por dia
        if (year1 != year2) {
            return Integer.compare(year1, year2); // Retorna a comparação de anos
        } else if (month1 != month2) {
            return Integer.compare(month1, month2); // Retorna a comparação de meses
        } else {
            return Integer.compare(day1, day2); // Retorna a comparação de dias
        }
    }

    // Método para ler o arquivo CSV e retornar os dados em um array
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][]; // Inicializa o array de dados
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a primeira linha como cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Divide os cabeçalhos
            data = new String[1][headers.length]; // Inicializa o array com cabeçalhos
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1; // Contador de linhas
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array para adicionar a nova linha
                data[rowCount] = record.stream().toArray(String[]::new); // Adiciona a linha lida
                rowCount++;
            }

            System.out.println("Total de linhas lidas: " + (rowCount - 1)); // Exibe o número total de linhas lidas
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return data; // Retorna os dados lidos
    }

    // Método para expandir o array existente
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] expanded = new String[newLength][original[0].length]; // Inicializa o novo array
        System.arraycopy(original, 0, expanded, 0, original.length); // Copia os dados do array original
        return expanded; // Retorna o array expandido
    }

    // Método para gravar os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT
                .withHeader(data[0])
                .withQuoteMode(QuoteMode.ALL))) {
            // Adiciona as linhas ao arquivo CSV
            for (int i = 1; i < data.length; i++) {
                csvPrinter.printRecord((Object[]) data[i]);
            }
            csvPrinter.flush(); // Garante que todos os dados sejam gravados
            System.out.println("Arquivo CSV gravado com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao gravar o arquivo: " + e.getMessage());
        }
    }

    // Método para limpar espaços em branco no início da coluna especificada
    private static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            data[i][columnIndex] = data[i][columnIndex].trim(); // Remove espaços em branco
        }
        System.out.println("Espaços limpos na coluna " + columnIndex + ".");
    }
}
