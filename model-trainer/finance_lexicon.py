class FinanceLexicon:

    def __init__(self):
        self.base_path = "lexicon/"
        self.positive_words_path = self.base_path + "positive.txt"
        self.negative_words_path = self.base_path + "negative.txt"
        self.litigious_words_path = self.base_path + "litigious.txt"
        self.strong_modal_words_path = self.base_path + "strong_modal.txt"
        self.weak_modal_words_path = self.base_path + "weak_modal.txt"
        self.uncertainty_words_path = self.base_path + "uncertainty.txt"
        self.constraining_words_path = self.base_path + "constraining.txt"

        self.positive_words = []
        self.negative_words = []
        self.litigious_words = []
        self.strong_modal_words = []
        self.weak_modal_words = []
        self.uncertainty_words = []
        self.constraining_words = []

    def _read_words(self, word_list):
        with open(self.positive_words_path) as f:
            for word in f:
                if "str" in word:
                    break
                word_list.append(word.replace("\n", "").lower())

    def read_positive_words(self):
        self._read_words(self.positive_words)

    def read_negative_words(self):
        self._read_words(self.negative_words)

    def read_litigious_words(self):
        self._read_words(self.litigious_words)

    def read_strong_modal_words(self):
        self._read_words(self.strong_modal_words)

    def read_weak_modal_words(self):
        self._read_words(self.weak_modal_words)

    def read_uncertainty_words(self):
        self._read_words(self.uncertainty_words)

    def read_constraining_words(self):
        self._read_words(self.constraining_words)


if __name__ == "__main__":
    lexicon = FinanceLexicon()
    lexicon.read_positive_words()
    print()
