namespace backend.ModelsDto;

public class PostPageDto
{
    public List<PostDto> Posts { get; set; }
    public int CurrentPage { get; set; }
    public int NumberOfPages { get; set; }
}