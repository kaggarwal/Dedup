word_lists=word_list_crypto.txt word_list_design_patterns.txt \
	word_list_java.txt word_list_networking.txt word_list_patterns.txt \
	word_list_pragmatic.txt 


all: $(word_lists) word_list_general.txt

word_list_crypto.txt: crypto.txt
	get_word_lists crypto.txt

word_list_design_patterns.txt: design_patterns.txt
	get_word_lists design_patterns.txt

word_list_java.txt: java.txt
	get_word_lists java.txt

word_list_networking.txt: networking.txt
	get_word_lists networking.txt

word_list_patterns.txt: patterns.txt
	get_word_lists patterns.txt

word_list_pragmatic.txt: pragmatic.txt
	get_word_lists pragmatic.txt

word_list_general.txt: $(word_lists)
	cat $(word_lists) > word_list_general.txt

clean:
	rm word_list*
