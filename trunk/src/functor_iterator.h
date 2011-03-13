#ifndef _FUNCTOR_ITERATOR_H_
#define _FUNCTOR_ITERATOR_H_

#include <vector>

template<typename Funct>
class functor_iterator {
    Funct _funct;
public:
    typedef typename Funct::value_type value_type;
    functor_iterator(const Funct& funct): _funct(funct) {}
    functor_iterator& operator=(const value_type& elem) { _funct(elem); return *this; }
    functor_iterator& operator*() { return *this; }
    functor_iterator& operator++() { return *this; }
    functor_iterator operator++(int) { return *this; }
};

#endif
