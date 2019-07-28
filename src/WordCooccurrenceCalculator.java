import javafx.util.Pair;
import java.util.*;

public class WordCooccurrenceCalculator
{
    private Document document = null;

    /**
     * 2차원 희소 행렬을 구현한 {@link HashMap}
     */
    HashMap<MatrixKey, Integer> cooccurrenceMatrix = null;

    /**
     * key: {@link Term}<br>
     * value: 빈도수에 따라 정렬된 {@link Term}, 빈도수 쌍의 {@link ArrayList}
     */
    HashMap<Term, ArrayList<Pair<Term, Integer>>> orderedCooccurrenceListMap = null;

    public WordCooccurrenceCalculator() {}

    public WordCooccurrenceCalculator(final Document document, final int windowSize)
    {
        setDocument(document, windowSize);
    }

    public void setDocument(final Document document, final int windowSize)
    {
        this.document = document;

        cooccurrenceMatrix = buildMatrix(windowSize);
        orderedCooccurrenceListMap = buildOrderedKeyListMap();
    }

    public List<Pair<Term, Integer>> getOrderedCooccurrenceList(final String termContent)
    {
        return orderedCooccurrenceListMap.get(new Term(termContent));
    }

    public List<Pair<Term, Integer>> getOrderedCooccurrenceList(final Term term)
    {
        return orderedCooccurrenceListMap.get(term);
    }

    /**
     * 희소 행렬을 만든다.
     * @return 희소 행렬
     */
    private HashMap<MatrixKey, Integer> buildMatrix(final int windowSize)
    {
        Queue<Term> queue = new ArrayDeque<>();
        HashMap<MatrixKey, Integer> retVal = new HashMap<>();

        document.getTermList().forEach(term1 ->
        {
            if (!queue.isEmpty())
            {
                queue.forEach(term2 ->
                {
                    if (term1.equals(term2))
                        return;

                    final MatrixKey MATRIX_KEY = new MatrixKey(term1, term2);

                    retVal.computeIfPresent(MATRIX_KEY, (key, value) -> ++value);
                    retVal.putIfAbsent(MATRIX_KEY, 1);
                });
            }

            queue.add(term1);

            if (queue.size() >= (windowSize + 1))
                queue.poll();
        });

        return retVal;
    }

    /**
     * 만들어진 희소 행렬을 이용하여 {@link #orderedCooccurrenceListMap}을 생성한다.
     * @return {@link #orderedCooccurrenceListMap}
     */
    private HashMap<Term, ArrayList<Pair<Term, Integer>>> buildOrderedKeyListMap()
    {
        HashMap<Term, ArrayList<Pair<Term, Integer>>> retVal = new HashMap<>();

        cooccurrenceMatrix.forEach((matrixKey, frequency) ->
        {
            final Term TERM1 = matrixKey.getTerm1();
            final Term TERM2 = matrixKey.getTerm2();
            final Pair<Term, Integer> TERM1_FREQUENCY = new Pair<>(TERM1, frequency);
            final Pair<Term, Integer> TERM2_FREQUENCY = new Pair<>(TERM2, frequency);

            if (retVal.containsKey(TERM1))
                retVal.get(TERM1).add(TERM2_FREQUENCY);
            else
            {
                ArrayList<Pair<Term, Integer>> newVal = new ArrayList<>();
                newVal.add(TERM2_FREQUENCY);

                retVal.put(TERM1, newVal);
            }

            if (retVal.containsKey(TERM2))
                retVal.get(TERM2).add(TERM1_FREQUENCY);
            else
            {
                ArrayList<Pair<Term, Integer>> newVal = new ArrayList<>();
                newVal.add(TERM1_FREQUENCY);

                retVal.put(TERM2, newVal);
            }
        });

        retVal.forEach((term1, pairs) -> Collections.sort(pairs, (lhs, rhs) ->
        {
            int lhsVal = lhs.getValue();
            int rhsVal = rhs.getValue();

            if (lhsVal < rhsVal)
                return 1;
            else if (lhsVal == rhsVal)
                return 0;

            return -1;
        }));

        return retVal;
    }
}
