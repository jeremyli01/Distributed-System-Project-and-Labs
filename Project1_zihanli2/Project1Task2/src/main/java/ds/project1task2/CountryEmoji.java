package ds.project1task2;

public class CountryEmoji {
    private String country;
    private String emoji;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public CountryEmoji(String country, String emoji) {
        this.country = country;
        this.emoji = emoji;
    }
}

