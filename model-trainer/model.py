from nlp.keras.skipgram import SkipGram
from nlp.keras.inference import Inference
from nlp.keras.finance_lexicon import FinanceLexicon


class Model:

    def __init__(self):
        self.file_name = "../../files/alphabet10k.txt"
        self.file_to_dump = "vectors.txt"
        self.iterations = 10
        self.skipGram = SkipGram(self.file_name)
        self.lexicon = FinanceLexicon()
        self.word_vectors = self.skipGram.load_vectors(self.file_to_dump)

    def initialize_vocab(self):
        self.skipGram.initialize_vocab()

    def run_training(self):
        self.skipGram.execute(self.iterations, self.file_to_dump)

    def get_keywords_for_word(self, word):
        infer = Inference(self.word_vectors)

        words_with_distances = []
        for vocab_word, freq in self.skipGram.tokenizer.word_index.items():
            dist = infer.get_cosine_distance(word, vocab_word)
            words_with_distances.append((vocab_word, dist))

        words_with_distances = sorted(words_with_distances, key=lambda tup: tup[1], reverse=True)

        if len(words_with_distances) >= 20:
            return words_with_distances[:20]
        else:
            return words_with_distances

    def get_keywords_for_lexicon_words(self, words_dict, words):
        for word in words:

            found = False
            for vocab_word, freq in self.skipGram.tokenizer.word_index.items():
                if vocab_word == word:
                    found = True
                    break

            if found:
                print("word found - " + word)
                words_dict[word] = self.get_keywords_for_word(word)
        return words_dict

    def get_keywords_for_positive_words(self):
        self.lexicon.read_positive_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.positive_words)

    def get_keywords_for_negative_words(self):
        self.lexicon.read_negative_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.negative_words)

    def get_keywords_for_litigious_words(self):
        self.lexicon.read_litigious_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.litigious_words)

    def get_keywords_for_strong_modal_words(self):
        self.lexicon.read_strong_modal_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.strong_modal_words)

    def get_keywords_for_weak_modal_words(self):
        self.lexicon.read_weak_modal_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.weak_modal_words)

    def get_keywords_for_uncertainty_words(self):
        self.lexicon.read_uncertainty_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.uncertainty_words)

    def get_keywords_for_constraining_words(self):
        self.lexicon.read_constraining_words()
        return self.get_keywords_for_lexicon_words({}, self.lexicon.constraining_words)


if __name__ == "__main__":
    model = Model()
#    model.run_training()
    model.initialize_vocab()
    print(model.get_keywords_for_positive_words())
