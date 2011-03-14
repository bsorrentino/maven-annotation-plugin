#ifndef _FUNCTOR_ITERATOR_H_
#define _FUNCTOR_ITERATOR_H_

template<typename F, typename V>
class functor_iterator {
    F _funct;
public:
    functor_iterator(const F& funct): _funct(funct) {}
    functor_iterator& operator=(const V& elem) { _funct(elem); return *this; }
    functor_iterator& operator*() { return *this; }
    functor_iterator& operator++() { return *this; }
    functor_iterator operator++(int) { return *this; }
};

#endif
