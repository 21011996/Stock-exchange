package files;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class File {
    String name;
    int price;

    public File(String name, int price) {

        this.name = name;
        this.price = price;
    }

    public static File parseFile(String s) {
        String[] nameAndPrice = s.split("=");
        return new File(nameAndPrice[0], Integer.parseInt(nameAndPrice[1]));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + "=" + price;
    }
}
