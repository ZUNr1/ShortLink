import request from "./request.ts";
import type {LinkResponse, SwitchUrlRequest} from "../types";


export const linkApi = {
    // 获取短链详情
    getLinkByShortCode(shortCode: string): Promise<LinkResponse> {
        return request.get(`/api/link/find/${shortCode}`)
    },

    // 创建或获取短链
    switchUrl(data: SwitchUrlRequest): Promise<string> {
        return request.post('/api/link/switch', data)
    }
}