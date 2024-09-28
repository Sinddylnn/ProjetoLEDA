package main.java.ordenacao_channel_title.piorCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class MergeSortPC {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("C:", "Users", "Notebook - Eric", "IdeaProjects", "ProjetoLEDA", "src", "main", "java", "database", "ordenatedChannel_title", "videos_T1_channel_title_mergeSort_medioCaso.csv");
        Path outputPath = Paths.get("C:", "Users", "Notebook - Eric", "IdeaProjects", "ProjetoLEDA", "src", "main", "java", "database", "ordenatedChannel_title", "videos_T1_channel_title_mergeSort_piorCaso.csv");
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
                    sortByChannelTitle(data, columnIndex);
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

    // Verifica se os dados já estão ordenados na coluna especificada (decrescente)
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

            // Mudança: Verifica se os dados estão em ordem decrescente
            if (previousValue.compareToIgnoreCase(currentValue) < 0) {
                return false; // Retorna falso se não estiver em ordem decrescente
            }
        }
        return true;
    }

    // Lê os dados de um arquivo CSV e retorna um array bidimensional com as linhas
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][]; // Inicializa o array de dados
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê o cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Divide o cabeçalho em colunas
            data = new String[1][headers.length]; // Cria o array com o cabeçalho
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1; // Contador de linhas
            for (CSVRecord record : csvParser) { // Lê cada registro no CSV
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array para adicionar nova linha
                data[rowCount] = record.stream().toArray(String[]::new); // Armazena a linha no array
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
        String[][] newArray = new String[newSize][]; // Cria um novo array com o novo tamanho
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia os dados do array original
        return newArray; // Retorna o novo array
    }

    // Remove espaços iniciais de uma coluna específica em cada linha dos dados
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Removendo espaços iniciais na coluna " + columnIndex + "...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = removeLeadingAndTrailingSpaces(row[columnIndex]); // Limpa espaços
                System.out.println("Linha " + i + " após limpeza: " + Arrays.toString(row));
            }
        }
    }

    // Remove espaços em branco do início e do fim de uma string
    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    // Preenche colunas ausentes nas linhas, garantindo que todas tenham o mesmo número de colunas
    public static void fillMissingColumns(String[][] data) {
        String[] headers = data[0]; // Obtém os cabeçalhos
        int numColumns = headers.length; // Número de colunas

        System.out.println("Preenchendo colunas ausentes...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length < numColumns) { // Se a linha tiver menos colunas
                String[] newRow = new String[numColumns]; // Cria nova linha
                System.arraycopy(row, 0, newRow, 0, row.length); // Copia dados existentes
                for (int j = row.length; j < numColumns; j++) { // Preenche colunas restantes
                    newRow[j] = "";
                }
                data[i] = newRow; // Atualiza a linha no array
                System.out.println("Linha " + i + " após preenchimento: " + Arrays.toString(newRow));
            }
        }
    }

    // Ordena os dados pelo título do canal usando Merge Sort
    public static void sortByChannelTitle(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return;
        }

        String[] header = data[0]; // Armazena o cabeçalho
        String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length); // Copia dados a serem ordenados

        // Ordenar pelo título do canal
        System.out.println("Iniciando ordenação pelo título do canal...");
        dataToSort = mergeSort(dataToSort, columnIndex); // Chama o Merge Sort
        System.out.println("Ordenação concluída.");

        // Concatenar o cabeçalho de volta com os dados ordenados
        String[][] sortedData = new String[dataToSort.length + 1][header.length];
        sortedData[0] = header; // Adiciona cabeçalho
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length); // Adiciona dados ordenados

        System.arraycopy(sortedData, 0, data, 0, sortedData.length); // Atualiza dados com os dados ordenados
    }

    // Função de ordenação Merge Sort
    public static String[][] mergeSort(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            return data; // Se há 0 ou 1 elementos, já está ordenado
        }

        int mid = data.length / 2; // Encontra o meio do array
        String[][] left = Arrays.copyOfRange(data, 0, mid); // Cria o array da esquerda
        String[][] right = Arrays.copyOfRange(data, mid, data.length); // Cria o array da direita

        // Ordena as duas metades recursivamente
        left = mergeSort(left, columnIndex);
        right = mergeSort(right, columnIndex);

        // Combina as metades ordenadas
        return merge(left, right, columnIndex);
    }

    // Mescla duas partes ordenadas
    public static String[][] merge(String[][] left, String[][] right, int columnIndex) {
        String[][] merged = new String[left.length + right.length][]; // Array para armazenar a mescla
        int leftIndex = 0, rightIndex = 0, mergedIndex = 0;

        // Mescla os arrays
        while (leftIndex < left.length && rightIndex < right.length) {
            String leftValue = left[leftIndex][columnIndex].trim();
            String rightValue = right[rightIndex][columnIndex].trim();

            // Comparar os valores em ordem decrescente
            if (leftValue.compareToIgnoreCase(rightValue) > 0) {
                merged[mergedIndex++] = left[leftIndex++];
            } else {
                merged[mergedIndex++] = right[rightIndex++];
            }
        }

        // Adiciona os elementos restantes
        while (leftIndex < left.length) {
            merged[mergedIndex++] = left[leftIndex++];
        }

        while (rightIndex < right.length) {
            merged[mergedIndex++] = right[rightIndex++];
        }

        return merged; // Retorna o array mesclado
    }

    // Escreve os dados ordenados em um arquivo CSV
    public static void writeCsv(String[][] data, Path outputPath) {
        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            for (String[] row : data) {
                writer.write(String.join(",", row) + "\n"); // Junta as colunas por vírgula
            }
            System.out.println("Arquivo salvo em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
