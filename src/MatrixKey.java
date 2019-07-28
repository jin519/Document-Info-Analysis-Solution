import java.util.HashSet;

/**
 * {@link java.util.HashMap}으로 희소행렬을 구현하기 위한 보조 클래스<br>
 * 동일한 {@link Term} 집합을 가진다면, 같은 객체로 인식한다.<br><br>
 *
 * 참고: (A, B) == (B, A)
 */
public class MatrixKey
{
    private Term term1;
    private Term term2;
    private HashSet<Term> termSet = new HashSet<>();

    public MatrixKey(final Term term1, final Term term2)
    {
        this.term1 = term1;
        this.term2 = term2;

        termSet.add(term1);
        termSet.add(term2);
    }

    public Term getTerm1()
    {
        return term1;
    }

    public Term getTerm2() {
        return term2;
    }

    public Term getAnother(final Term term)
    {
        if (term.equals(term1))
            return term2;

        return term1;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MatrixKey))
            return false;

        MatrixKey matrixKey = (MatrixKey) o;

        return termSet.equals(matrixKey.termSet);
    }

    /**
     * term1과 term2의 위치에 상관 없이 동일한 hash code를 반환한다.
     * @return term1, term2 hash code의 합
     */
    @Override
    public int hashCode()
    {
        return termSet.hashCode();
    }

    @Override
    public String toString()
    {
        return "MatrixKey{" +
                "term1=" + term1 +
                ", term2=" + term2 +
                '}';
    }
}
