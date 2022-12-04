namespace backend.ModelsDto;

public class AddPostDto
{
    public string Location { get; set; } = string.Empty;
    public string Caption { get; set; } = string.Empty;
    public string Latitude { get; set; }
    public string Longitude { get; set; }
}