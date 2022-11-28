namespace backend.ModelsDto;

public class ChangePasswordDto
{
    public string CurrentPassword { get; set; }
    public string NewPassword { get; set; }
}