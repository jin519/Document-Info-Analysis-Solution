import java.util.Objects;

public class Term implements Comparable<Term>
{
    /**
     * Term 내용
     */
    private String content;

    /**
     * Term 가중치
     */
    private Double weight = 1.0;

    public Term(final String content)
    {
        setContent(content);
    }

    public Term(final String content, final double weight)
    {
        setContent(content);
        setWeight(weight);
    }

    public String getContent()
    {
        return content;
    }

    public Double getWeight()
    {
        return weight;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    public void setWeight(final double weight)
    {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;

        if (!(object instanceof Term))
            return false;

        final Term OBJECT = (Term)object;

        return content.equals(OBJECT.content);
    }

    @Override
    public int hashCode()
    {
        return content.hashCode();
    }

    @Override
    public int compareTo(Term term)
    {
        return weight.compareTo(term.getWeight());
    }

    @Override
    public String toString() {
        return "Term{" +
                "content='" + content + '\'' +
                ", weight=" + weight +
                '}';
    }
}
