namespace backend.ModelsDto;

public class PostDto
{
    public int Id { get; set; }
    public string Owner { get; set; }
    public long Date { get; set; }
    public string Location { get; set; }
    public string Caption { get; set; }
    public string Longitude { get; set; }
    public string Latitude { get; set; }
    public int NumberOfLikes { get; set; }
    public int NumberOfComments { get; set; }
    public bool LikedByMe { get; set; }
}