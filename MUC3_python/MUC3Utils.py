from html.parser import HTMLParser


class TextExtractor(HTMLParser):
    """
    Extracts text from the body of an html file of the MUC3 dataset
    """
    def __init__(self):
        super().__init__()
        self.data = ""
        self.isBody = False

    def error(self, message):
        pass

    def handle_starttag(self, tag, attrs):
        if tag == "body":
            self.isBody = True

    def handle_endtag(self, tag):
        if tag == "body":
            self.isBody = False

    # noinspection PyUnresolvedReferences
    def handle_data(self, data):
        if self.isBody:
            self.data += data


# noinspection PyAttributeOutsideInit
class CategoryExtractor(HTMLParser):
    """
    Extracts the DC.coverage category/tag from a text of the MUC3 dataset
    """
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
