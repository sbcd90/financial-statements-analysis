from model import Model
from finance_lexicon import FinanceLexicon
from postprocess import PostProcess

class DocumentsPreparator:

    def __init__(self):
        self.file_prefix_range = (2005, 2019)
        self.year_range = 5
        self.lexicon = FinanceLexicon()
        self.lexicon.read_positive_words()
        self.lexicon.read_negative_words()
        self.lexicon.read_litigious_words()
        self.lexicon.read_strong_modal_words()
        self.lexicon.read_weak_modal_words()
        self.lexicon.read_constraining_words()
        self.lexicon.read_uncertainty_words()

    def generate_file_names_by_year(self, year):
        file_names = []
        for i in range(1, self.year_range):
            file_names.append(str(year) + str(i) + ".txt")
        return file_names

    def create_model_and_run_training(self, file_name, year):
        model = Model(file_name, file_name)
        v, nb_samples = model.initialize_vocab()
        model.run_training(v, nb_samples)

    def generate_words_per_document(self, model):
        model.initialize_vocab()
        words_list = model.get_keywords_for_positive_words()
        words_list.extend(model.get_keywords_for_negative_words())
        words_list.extend(model.get_keywords_for_litigious_words())
        words_list.extend(model.get_keywords_for_strong_modal_words())
        words_list.extend(model.get_keywords_for_weak_modal_words())
        words_list.extend(model.get_keywords_for_constraining_words())
        words_list.extend(model.get_keywords_for_uncertainty_words())
        return words_list

    def get_keywords_for_document(self, file_name, file_to_dump):
        model = Model(file_name, file_to_dump)
        words_tuple_list = self.generate_words_per_document(model)
        return words_tuple_list, " ".join(list(map(lambda x: x[1], words_tuple_list)))

    def get_keywords_for_year(self, year):
        year_docs_word_tuple_dict = {}
        year_words_list = []
        for i in range(1, self.year_range):
            file_name = str(year) + str(i) + ".txt"
            self.create_model_and_run_training(file_name, year)
            words_tuple_list, words = self.get_keywords_for_document(file_name, file_name)
            year_docs_word_tuple_dict[file_name] = words_tuple_list
            year_words_list.append(words)
        return year_docs_word_tuple_dict, year_words_list

    def get_imp_keywords_for_year(self, year):
        year_docs_word_tuple_dict, year_words_list = self.get_keywords_for_year(year)
        postProcess = PostProcess()
        return year_docs_word_tuple_dict, postProcess.find_common_words(year_words_list, 1000)

    def get_score_by_year(self, year):
        year_docs_word_tuple_dict, final_words_list = self.get_imp_keywords_for_year(year)
        print(self.calculate_scores_by_year(year_docs_word_tuple_dict, final_words_list))
        return -1

    def calculate_scores_by_year(self, word_tuple_dict, words_list):
        original_words = []
        for word in words_list:
            for words_tuple in word_tuple_dict.values():
                for word_tuple in words_tuple:
                    if word_tuple[1] == word:
                        original_words.append(word_tuple[0])
                        break

        cp = 0
        cn = 0
        cl = 0
        csm = 0
        cwm = 0
        cc = 0
        cu = 0
        for word in original_words:
            for positive_word in self.lexicon.positive_words:
                if positive_word == word:
                    cp += 1
                    break

            for negative_word in self.lexicon.negative_words:
                if negative_word == word:
                    cn += 1
                    break

            for litigious_word in self.lexicon.litigious_words:
                if litigious_word == word:
                    cl += 1
                    break

            for strong_modal_word in self.lexicon.strong_modal_words:
                if strong_modal_word == word:
                    csm += 1
                    break

            for weak_modal_word in self.lexicon.weak_modal_words:
                if weak_modal_word == word:
                    cwm += 1
                    break

            for constraining_word in self.lexicon.constraining_words:
                if constraining_word == word:
                    cc += 1
                    break

            for uncertainty_word in self.lexicon.uncertainty_words:
                if uncertainty_word == word:
                    cu += 1
                    break
        return cp, cn, cl, csm, cwm, cc, cu


if __name__ == "__main__":
    docsPreparator = DocumentsPreparator()
    docsPreparator.get_score_by_year(2007)