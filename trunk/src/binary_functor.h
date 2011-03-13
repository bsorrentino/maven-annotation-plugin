#ifndef _BINARY_FUNCTOR_H_
#define _BINARY_FUNCTOR_H_

#include <vector>

template<typename Elem, typename Iter, typename Cmp>
class binary_functor {
    Iter _iter;
    Cmp _cmp;
    std::vector<Elem> values;
public:
    typedef Elem value_type;
    binary_functor(const Iter& iter, const Cmp& cmp): _iter(iter), _cmp(cmp) {}
    void operator()(const Elem& elem) {
        transform(values.begin(), values.end(), _iter, std::bind2nd(_cmp, elem));
        values.push_back(elem);
    }
};

#endif
