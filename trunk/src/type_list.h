#ifndef _TYPE_LIST_H_
#define _TYPE_LIST_H_

template<typename H, typename T>
struct cons {
    typedef H head;
    typedef T tail;
    enum { size = T::size + 1 };
};
struct nil {
    enum { size = 0 };
};

template<typename T>
struct type_traits {
    typedef T* ptr_type;
    typedef const T* const_ptr_type;
    typedef T& ref_type;
    typedef const T& const_ref_type;
};

template<typename T>
struct type_traits<T*> {
    typedef T* ptr_type;
    typedef const T* const_ptr_type;
};

template<typename F, typename T>
class conversion {
    typedef char small;
    class big { char dummy[2]; };
    static small test(T);
    static big test(...);
    static F makeF();
public:
    enum { exists = sizeof(test(makeF())) == sizeof(small), same = false };
};

template<typename T>
struct conversion<T, T> {
    enum { exists = true, same = true };
};

#define SUPER_SUB_CLASS(A, B) (conversion<const B*, const A*>::exists \
    && !conversion<const A*, const void*>::same)

template<bool, typename, typename> struct choose;
template<typename T, typename F>
struct choose<true, T, F> {
    typedef T result;
};
template<typename T, typename F>
struct choose<false, T, F> {
    typedef F result;
};

template<typename T> struct type_sort;
template<> struct type_sort<nil> {
    typedef nil result;
};
template<typename H, typename T>
class type_sort< cons<H, T> > {
    template<typename, typename> struct insert;
    template<typename F> struct insert<F, nil> {
        typedef cons<F, nil> result;
    };
    template<typename F, typename L, typename R>
    struct insert<F, cons<L, R> > {
        typedef typename choose
            <SUPER_SUB_CLASS(F, L)
                , cons<F, cons<L, R> >
                , cons<L, typename insert<F, R>::result>
            >::result result;
    };
public:
    typedef typename insert<H, typename type_sort<T>::result>::result result;
};

template<typename F, typename T> struct filter;
template<typename F> struct filter<F, nil> {
    typedef nil descendant;
    typedef nil not_descendant;
};
template<typename F, typename H, typename T>
class filter<F, cons<H, T> > {
    typedef typename filter<F, T>::descendant tmp_d;
    typedef typename filter<F, T>::not_descendant tmp_n;
public:
    typedef typename choose<SUPER_SUB_CLASS(F, H), cons<H, tmp_d>, tmp_d>::result descendant;
    typedef typename choose<SUPER_SUB_CLASS(F, H), tmp_n, cons<H, tmp_n> >::result not_descendant;
};

#endif
