import os
import csv
from html.parser import HTMLParser


# noinspection PyAttributeOutsideInit
class MUC3Parser(HTMLParser):
    def error(self, message):
        pass

    def handle_starttag(self, tag, attrs):
        if tag == "meta":
            result = [attr for attr in attrs if
                      (attr[0] == "name" and attr[1] == "DC.coverage")]
            if len(result) > 0:
                # This is the DC.coverage <meta> tag, get the category
                content = [attr for attr in attrs if (attr[0] == "content")]

                # Category is in the (1) result, the 2nd element of the tuple
                category = content[0][1]

                self.data = category


def get_text_type(path, file):
    # Open the file
    with open(path + file, "r") as f:
        content = f.read()

        # Create HTML parser to get the dc.content type
        parser = MUC3Parser()
        parser.feed(content)

        return parser.data


def get_text_categories(path):
    # Dictionary to keep types for each text
    text_types = {}

    files = os.listdir(path)

    for file in files:
        text_type = get_text_type(path, file)
        text_types[file] = text_type

    return text_types


def main():
    path = "../texts/input/"
    csv_path = "../out.csv"

    # Get the "category" of each text from the DC.coverage attribute of MUC3
    text_categories = get_text_categories(path)

    # Check that the CSV to read exists
    if os.path.isfile(csv_path):
        with open(csv_path) as f:
            # Read the CSV
            reader = csv.reader(f, delimiter=",", quoting=csv.QUOTE_NONE)

            for row in reader:
                # TODO: add info to the row and print to new file
                print(row)


if __name__ == '__main__':
    main()
