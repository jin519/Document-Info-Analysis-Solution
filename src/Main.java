import javafx.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main
{
    private static void initResources(
            List<Document> docList, TFIDFCalculator tfIdfCalculator,
            Map<String, WordCooccurrenceCalculator> wcMap, final int wcWinSize,
            Map<String, List<Pair<Document, Double>>> csMap, final int csNumFeaturesForCalculating)
    {
        tfIdfCalculator.buildCacheData();

        for (Document document : docList)
        {
            String docID = document.getID();
            wcMap.put(docID, new WordCooccurrenceCalculator(document, wcWinSize));
            csMap.put(docID, CosineSimilarityCalculator.calculate(document, docList, csNumFeaturesForCalculating));
        }
    }

    private static boolean checkTokenSize(String[] input)
    {
        return (input.length == 2);
    }

    private static void printTitle()
    {
        System.out.println("**********************************************");
        System.out.println("\t신입사원 교육 - 정보검색 솔루션 데모 프로그램");
        System.out.println("\t작성자: 원진");
        System.out.println("\t작성일: 19.07.26");
        System.out.println("**********************************************");
    }

    private static void printMenuBoard()
    {
        System.out.println("\n원하시는 작업을 선택하세요.");
        System.out.println("1. 문서별 TF-IDF 스코어 출력 (입력 예: 1 A)");
        System.out.println("2. Word Co-occurrence 출력 (입력 예: 2 A)");
        System.out.println("3. TF-IDF 스코어 기반 코사인 유사도 출력 (입력 예: 3 sample01.data)");
        System.out.println("4. 분석할 문서 추가 (입력 예: 4 directory)");
        System.out.println("5. 데모 종료");
    }

    private static void printTfIdfScores(String content, List<Document> documentList, TFIDFCalculator tfIdfCalculator)
    {
        for (Document document : documentList)
        {
            final String docID = document.getID();
            System.out.println(docID + ": " + tfIdfCalculator.calculate(content, docID));
        }
    }

    private static void printWordCooccurrence(
            String content, List<Document> documentList, Map<String, WordCooccurrenceCalculator> wcMap)
    {
        for (Document document : documentList)
        {
            final String docID = document.getID();
            System.out.println(docID + ": " + wcMap.get(docID).getOrderedCooccurrenceList(content));
        }
    }

    private static void printCosineSimilarities(
            String docID, Map<String, List<Pair<Document, Double>>> csMap)
    {
        if (!csMap.containsKey(docID))
        {
            System.out.println("해당 문서는 현재 로드되어 있지 않습니다.");
            return;
        }

        csMap.get(docID).forEach(pair ->
        {
            String targetDocID = pair.getKey().getID();
            double targetCS = pair.getValue();

            System.out.println(targetDocID + ": " + targetCS);
        });
    }

    public static void main(String[] args) throws Exception
    {
        List<Document> documentList = Document.batchRead("data/");
        TFIDFCalculator tfIdfCalculator = new TFIDFCalculator(documentList);

        // WordCooccurrenceCalculator list map (docID, WordCooccurrenceCalculator)
        Map<String, WordCooccurrenceCalculator> wcMap = new HashMap<>();

        // 코사인 유사도 list map (docID, 코사인 유사도 리스트)
        Map<String, List<Pair<Document, Double>>> csMap = new HashMap<>();

        initResources(documentList, tfIdfCalculator, wcMap, 2, csMap, 5);

        Scanner scanner = new Scanner(System.in);

        printTitle();
        while (true)
        {
            printMenuBoard();
            System.out.print(">> ");
            String[] input = scanner.nextLine().split(" ");

            if ((input.length == 1) && input[0].equals("5"))
                break;

            if (!checkTokenSize(input))
            {
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                continue;
            }

            int menuNum = Integer.parseInt(input[0]);
            switch (menuNum)
            {
                case 1:
                {
                    String content = input[1];
                    printTfIdfScores(content, documentList, tfIdfCalculator);
                }
                    break;

                case 2:
                {
                    String content = input[1];
                    printWordCooccurrence(content, documentList, wcMap);
                }
                    break;

                case 3:
                {
                    String docID = input[1];
                    printCosineSimilarities(docID, csMap);
                }
                    break;

                case 4:
                {
                    String directory = input[1];
                    List<Document> newDocList = Document.batchRead(directory);
                    documentList.addAll(newDocList);

                    tfIdfCalculator.setDocumentList(documentList);
                    wcMap.clear();
                    csMap.clear();

                    initResources(documentList, tfIdfCalculator, wcMap, 2, csMap, 5);
                    System.out.println(
                            "디렉토리 [" + directory + "]에 존재하는 " +
                                    newDocList.size() + "개의 문서를 추가로 로드하였습니다.");
                }
                    break;

                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }

        System.out.println("프로그램을 종료합니다.");
    }
}
