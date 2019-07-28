import javafx.util.Pair;

import java.util.*;

/**
 * TF-IDF의 계산을 돕는 클래스<br><br>
 *
 * 참고:<br>
 * TF-IDF(Term Frequency - Inverse Document Frequency): 정보 검색과 텍스트 마이닝에서 주로 사용하는 기법으로,
 * 여러 문서로 이루어진 문서집합이 있을 때 특정 단어가 특정 문서 내에서 얼마나 중요한 것인지를 나타내는 통계적 수치이다.
 *
 * @see Term
 * @see Document
 */
public class TFIDFCalculator
{
    /**
     * key: {@link Term} content
     * value: (key: {@link Document} ID, value: TF_IDF 값) 리스트
     */
    private HashMap<String, HashMap<String, Double>> tfIdfMap = new HashMap<>();

    /**
     * {@link Document} 리스트
     */
    private List<Document> documentList = null;

    /**
     * {@link Document} 리스트 내 모든 {@link Term} contents의 문서 별 TF-IDF 값을 구축할 것인지에 대한 여부
     */
    private boolean cached = false;

    public TFIDFCalculator() {}

    public TFIDFCalculator(final List<Document> documentList)
    {
        setDocumentList(documentList);
    }

    /**
     * {@link #buildTfIdfMap()} 함수를 명시적으로 호출한다.
     */
    public void buildCacheData()
    {
        buildTfIdfMap();
        cached = true;
    }

    public void setDocumentList(final List<Document> documentList)
    {
        this.documentList = documentList;

        if (cached)
        {
            tfIdfMap.clear();
            cached = false;
        }
    }

    public double calculate(final String content, final String documentID)
    {
        if (!tfIdfMap.containsKey(content))
            tfIdfMap.put(content, buildTfIdfMapForSingleContent(content));

        return tfIdfMap.get(content).get(documentID);
    }

    // 반환 값: content, tfIdf 쌍 (tfIdf 값에 대한 오름차순 정렬)
    public List<Pair<String, Double>> rank(final String documentID, final int listSize)
    {
        if (!cached)
            buildCacheData();

        List<Pair<String, Double>> retVal = new ArrayList<>();

        tfIdfMap.forEach((content, tfIdfMap) ->
                tfIdfMap.forEach((docID, aDouble) ->
                {
                    if (docID.equals(documentID))
                        retVal.add(new Pair<>(content, aDouble));
                }));

        retVal.sort((pair1, pair2) ->
        {
            final double VAL1 = pair1.getValue();
            final double VAL2 = pair2.getValue();

            if (VAL1 < VAL2)
                return 1;
            else if (VAL1 > VAL2)
                return -1;

            return 0;
        });

        return retVal.subList(0, Math.min(listSize, retVal.size()));
    }

    /**
     * {@link Document} 리스트 내 모든 {@link Term} contents의 문서 별 TF-IDF 값을 구축한다.
     */
    private void buildTfIdfMap()
    {
        documentList.forEach(document -> document.getTermList().forEach(term ->
        {
            final String CONTENT = term.getContent();

            if (!tfIdfMap.containsKey(CONTENT))
                tfIdfMap.put(CONTENT, buildTfIdfMapForSingleContent(CONTENT));
        }));
    }

    /**
     * {@link Term} content에 대한 TF-IDF 맵을 생성한다.
     * @param content {@link Term} content
     * @return TF-IDF 맵
     */
    private HashMap<String, Double> buildTfIdfMapForSingleContent(final String content)
    {
        HashMap<String, Double> retVal = new HashMap<>();
        final double[] df = {0.0};

        documentList.forEach(document ->
        {
            final double[] numTerms = {0.0};
            final List<Term> TERM_LIST = document.getTermList();

            TERM_LIST.forEach(term ->
            {
                final String CONTENT = term.getContent();

                if (CONTENT.equals(content))
                    numTerms[0] += 1.0;
            });

            if (numTerms[0] > 0.0)
                df[0] += 1.0;

            final double TF = (numTerms[0] / (double)TERM_LIST.size());
            retVal.put(document.getID(), TF);
        });

        final double IDF = Math.log(1.0 + ((double)documentList.size() / df[0]));
        retVal.replaceAll((documentName, tf) -> (tf * IDF));

        return retVal;
    }
}
