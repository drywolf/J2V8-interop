
import {J2V8} from './J2V8Interop';

export interface JsHeapEntry
{
    __ptr: number;
    __val: any;
}

declare type HeapEntries = {[key: number]: JsHeapEntry | null};

export class JsProxyHeap
{
    heapEntries: HeapEntries = {};

    putEntry(entry: JsHeapEntry)
    {
        if (this.heapEntries[entry.__ptr])
            throw new Error("Object is already on the heap");

        this.heapEntries[entry.__ptr] = entry;
    }

    // TODO: add concrete type for boxing (maybe this can be JsHeapEntry??)
    public getOrCreateInstance(boxedInstance: any)
    {
        let entry = this.heapEntries[boxedInstance.__ptr];

        if (!entry)
        {
            let clazz = J2V8.import(boxedInstance.__cls);

            if (clazz === null)
                throw new Error(`Unable to load type for boxed instance of type: ${boxedInstance.__cls}`);

            let instance = new clazz(boxedInstance);
            instance.__ptr = boxedInstance.__ptr;

            entry =
            {
                __ptr: boxedInstance.__ptr, // TODO: can use ES6 syntax if boxedInstance is typed ??
                __val: instance,
            }
            this.heapEntries[entry.__ptr] = entry;
        }

        return entry;
    }

    public static instance = new JsProxyHeap();
}
