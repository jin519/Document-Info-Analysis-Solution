import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Document
{
    /**
     * 문서 ID
     */
    final private String ID;

    /**
     * {@link Term} 리스트
     */
    private ArrayList<Term> termList = new ArrayList<>();

    /**
     * 디렉토리 내에 존재하는 모든 파일들을 읽어 {@link Document} 객체의 리스트로 반환한다.
     * @param directory 디렉토리 경로
     * @return {@link Document} 리스트
     * @throws IOException
     */
    public static List<Document> batchRead(final String directory) throws IOException
    {
        final File dir = new File(directory);

        ArrayList<Document> documentList = new ArrayList<>();

        if (!dir.isDirectory())
            documentList.add(new Document(directory, " "));
        else
        {
            for (final File file : dir.listFiles())
                documentList.add(new Document(file.getPath(), " "));
        }

        return documentList;
    }

    public Document(final Document document)
    {
        ID = document.ID;
        termList.addAll(document.termList);
    }

    public Document(final String path) throws IOException
    {
        this(path, " ");
    }

    public Document(final String path, final String delimiter) throws IOException
    {
        ID = parseID(path);
        load(path, delimiter);
    }

    public String getID()
    {
        return ID;
    }

    public List<Term> getTermList()
    {
        return termList;
    }

    private String parseID(final String path)
    {
        return path.substring((path.lastIndexOf(File.separator) + 1), path.length());
    }

    public void load(final String path, final String delimiter) throws IOException
    {
        loadTermList(path, delimiter);
    }

    private void loadTermList(final String path, final String delimiter) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            // UTF-8 포맷의 경우 파일의 시작에서 BOM(Byte Order Mark)가 등장한다.
            if (line.startsWith("\uFEFF"))
                line = line.substring(1);

            final String[] TERM_NAMES = line.split(delimiter);

            for (int i = 0; i < TERM_NAMES.length; ++i)
                termList.add(new Term(TERM_NAMES[i]));
        }

        bufferedReader.close();
    }
}
